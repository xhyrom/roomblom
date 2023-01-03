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

        event.deferReply().queue()

        val guildMusicManager = Bot.getLavaLinkManager().getGuildMusicManager(voiceChannel.guild)

        try {
            guildMusicManager.getLink().connect(voiceChannel)
        } catch (e: Exception) {
            return event.reply("${Bot.MASCOT} An error occurred while connecting to the voice channel.").setEphemeral(true).queue()
        }

        val tried = getSource(event.getOption("song")?.asString!!)

        guildMusicManager.getLink().restClient.loadItem(
            tried.toString(),
            object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    if (guildMusicManager.getQueue().getQueue().isEmpty()) {
                        event.hook.editOriginal("${Bot.MASCOT} Playing **${track.info.title}**.").queue()
                    } else {
                        event.hook.editOriginal("${Bot.MASCOT} Added **${track.info.title}** to queue.").queue()
                    }

                    guildMusicManager.getQueue().add(track)
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {
                    if (playlist.isSearchResult) {
                        if (guildMusicManager.getQueue().getQueue().isEmpty()) {
                            event.hook.editOriginal("${Bot.MASCOT} Playing **${playlist.tracks[0].info.title}**.").queue()
                        } else {
                            event.hook.editOriginal("${Bot.MASCOT} Added **${playlist.tracks[0].info.title}** to queue.").queue()
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
            return Source("ytsearch", query, mutableListOf("dzsearch", "scsearch", "spsearch"))
        }

        return Source(null, query, mutableListOf("ytsearch", "dzsearch", "scsearch", "spsearch"))
    }
}