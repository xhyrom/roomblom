package me.xhyrom.roomblom.commands

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import me.xhyrom.roomblom.Bot
import me.xhyrom.roomblom.api.structs.Command
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

private class Source(var provider: String?, val query: String, val remains: MutableList<String>) {
    override fun toString(): String {
        return if (provider == null) {
            query
        } else {
            "$provider:$query"
        }
    }
}

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

        val voiceChannel = event.member?.voiceState?.channel?.asVoiceChannel()
            ?: return event.reply("${Bot.MASCOT} You must be in a voice channel to use this command.").setEphemeral(true).queue()

        if (event.guild?.selfMember?.voiceState?.channel != null && event.guild?.selfMember?.voiceState?.channel != voiceChannel) {
            return event.reply("${Bot.MASCOT} You must be in the same voice channel as the bot to use this command.").setEphemeral(true).queue()
        }

        val guildMusicManager = Bot.getLavaLinkManager().getGuildMusicManager(voiceChannel.guild)

        try {
            guildMusicManager.getLink().connect(voiceChannel)
        } catch (e: Exception) {
            event.reply("${Bot.MASCOT} An error occurred while connecting to the voice channel.").setEphemeral(true).queue()
            return
        }

        event.deferReply().queue()

        val tried = getSource(event.getOption("song")?.asString!!)

        guildMusicManager.getLink().restClient.loadItem(
            tried.toString(),
            object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    var source = track.sourceManager.sourceName.replaceFirstChar { it.uppercase() }
                    if (source == "Youtube") source = "Spotify"

                    if (guildMusicManager.getQueue().getQueue().isEmpty()) {
                        event.hook.editOriginal("${Bot.MASCOT} Playing **${track.info.title}** from **${source}**.").queue()
                    } else {
                        event.hook.editOriginal("${Bot.MASCOT} Added **${track.info.title}** to queue from **${source}**.").queue()
                    }

                    guildMusicManager.getQueue().add(track)
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {
                    if (playlist.isSearchResult) {
                        var source = playlist.tracks[0].sourceManager.sourceName.replaceFirstChar { it.uppercase() }
                        if (source == "Youtube") source = "Spotify"

                        if (guildMusicManager.getQueue().getQueue().isEmpty()) {
                            event.hook.editOriginal("${Bot.MASCOT} Playing **${playlist.tracks[0].info.title}** from **${source}**.").queue()
                        } else {
                            event.hook.editOriginal("${Bot.MASCOT} Added **${playlist.tracks[0].info.title}** to queue (from **${source}**).").queue()
                        }

                        guildMusicManager.getQueue().add(playlist.tracks[0])
                    } else {
                        event.hook.editOriginal("${Bot.MASCOT} Added **${playlist.tracks.size}** tracks to queue.").queue()

                        playlist.tracks.forEach { guildMusicManager.getQueue().add(it) }
                    }
                }

                override fun noMatches() {
                    if (tried.remains.isNotEmpty()) {
                        tried.provider = tried.remains.removeFirst()

                        guildMusicManager.getLink().restClient.loadItem(
                            tried.toString(),
                            this
                        )
                        return
                    }

                    event.hook.editOriginal("${Bot.MASCOT} No matches found.").queue()
                }

                override fun loadFailed(exception: FriendlyException?) {
                    if (tried.remains.isNotEmpty()) {
                        tried.provider = tried.remains.removeFirst()

                        guildMusicManager.getLink().restClient.loadItem(
                            tried.toString(),
                            this
                        )
                        return
                    }

                    event.hook.editOriginal("${Bot.MASCOT} Failed to load track(s).").queue()
                }
            }
        )
    }

    private fun getSource(query: String): Source {
        if (!query.contains("http")) {
            return Source("ytsearch", query, mutableListOf("spsearch", "dzsearch", "scsearch"))
        }

        return Source(null, query, mutableListOf("ytsearch", "spsearch", "dzsearch", "scsearch"))
    }
}
