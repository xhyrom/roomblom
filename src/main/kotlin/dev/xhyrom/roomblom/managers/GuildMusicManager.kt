package dev.xhyrom.roomblom.managers

import dev.arbjerg.lavalink.protocol.v4.Message
import dev.arbjerg.lavalink.protocol.v4.Track
import dev.schlaubi.lavakord.audio.TrackEndEvent
import dev.schlaubi.lavakord.audio.on
import dev.schlaubi.lavakord.audio.player.Player
import dev.xhyrom.roomblom.Bot
import net.dv8tion.jda.api.entities.Guild
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class GuildMusicManager(private val guild: Guild) {
    private val link = Bot.getLavaLinkManager().getLavaLink().getLink(guild.id)
    private val queue = TrackQueue(this)
    private var loop = false

    fun getQueue() = queue
    fun getLink() = link
    fun getPlayer(): Player = link.player
    fun getCoroutineScope() = getPlayer().coroutineScope

    fun isLoop() = loop
    fun setLoop(loop: Boolean) {
        this.loop = loop
    }

    fun destroy() {
        Bot.getLavaLinkManager().getGuildMusicManagers().remove(guild.idLong)
    }
}

class TrackQueue(private val manager: GuildMusicManager) {
    private val queue: BlockingQueue<Track> = LinkedBlockingQueue()

    init {
        manager.getPlayer().on<TrackEndEvent> {
            if (this.reason == Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason.FINISHED)
                nextTrack()
        }
    }

    suspend fun add(track: Track) {
        if (manager.getPlayer().playingTrack == null) {
            manager.getPlayer().playTrack(track)
        } else {
            queue.offer(track)
        }
    }

    suspend fun nextTrack() {
        val track = queue.poll() ?: run {
            manager.getPlayer().stopTrack()

            manager.getLink().disconnectAudio()
            manager.destroy()
            return
        }

        manager.getPlayer().playTrack(track)

        if (manager.isLoop()) queue.offer(track)
    }

    fun getQueue(): BlockingQueue<Track> {
        return queue
    }
}