package me.xhyrom.mumblum.commands

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import me.xhyrom.mumblum.Bot
import me.xhyrom.mumblum.api.structs.Command
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class Play : Command(
    "play",
    "Play a song",
    listOf(
        OptionData(
            OptionType.STRING,
            "song",
            "The song to play",
            true
        )
    )
) {
    override fun execute(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()

        val voiceChannel = event.member?.voiceState?.channel?.asVoiceChannel()
            ?: return event.hook.editOriginal("You must be in a voice channel to use this command").queue()
        val guildMusicManager = Bot.getInstanceUnsafe().getLavaLinkManager().getGuildMusicManager(voiceChannel.guild)

        guildMusicManager.getLink().connect(voiceChannel)

        guildMusicManager.getLink().restClient.loadItem(
            getSource(event.getOption("song")?.asString!!),
            object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    event.hook.editOriginal("Playing ${track.info.title}").queue()

                    guildMusicManager.getQueue().add(track)
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {
                    if (playlist.isSearchResult) {
                        event.hook.editOriginal("Playing ${playlist.tracks[0].info.title}").queue()

                        guildMusicManager.getQueue().add(playlist.tracks[0])
                    } else {
                        event.hook.editOriginal("Added ${playlist.tracks.size} to queue").queue()

                        playlist.tracks.forEach { guildMusicManager.getQueue().add(it) }
                    }
                }

                override fun noMatches() {
                    event.hook.editOriginal("No matches found").queue()
                }

                override fun loadFailed(exception: FriendlyException?) {
                    println(exception?.message)
                    println(exception?.localizedMessage)
                    event.hook.editOriginal("Failed to load track").queue()
                }
            }
        )
    }

    private fun getSource(query: String): String {
        if (!query.contains("http")) {
            return "ytsearch:$query"
        }

        return query
    }
}