package dev.xhyrom.roomblom.commands

import dev.arbjerg.lavalink.protocol.v4.LoadResult
import dev.arbjerg.lavalink.protocol.v4.ResultStatus
import dev.schlaubi.lavakord.rest.loadItem
import dev.xhyrom.roomblom.Bot
import dev.xhyrom.roomblom.api.structs.Command
import dev.xhyrom.roomblom.managers.GuildMusicManager
import kotlinx.coroutines.launch
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
            ?: return event.reply("${Bot.MASCOT} You must be in a voice channel to use this command.")
                .setEphemeral(true).queue()

        if (event.guild?.selfMember?.voiceState?.channel != null && event.guild?.selfMember?.voiceState?.channel != voiceChannel) {
            return event.reply("${Bot.MASCOT} You must be in the same voice channel as the bot to use this command.")
                .setEphemeral(true).queue()
        }

        val guildMusicManager = Bot.getLavaLinkManager().getGuildMusicManager(voiceChannel.guild)

        guildMusicManager.getCoroutineScope().launch {
            try {
                guildMusicManager.getLink().connect(voiceChannel.id)
            } catch (e: Exception) {
                event.reply("${Bot.MASCOT} An error occurred while connecting to the voice channel.").setEphemeral(true)
                    .queue()
                return@launch
            }

            event.deferReply().queue()

            val tried = getSource(event.getOption("song")?.asString!!)

            load(tried, event, guildMusicManager)
        }
    }

    private suspend fun load(tried: Source, event: SlashCommandInteractionEvent, guildMusicManager: GuildMusicManager) {
        when (val item = guildMusicManager.getLink().loadItem(
            tried.toString(),
        )) {
            is LoadResult.TrackLoaded -> {
                if (guildMusicManager.getQueue().getQueue().isEmpty()) {
                    event.hook.editOriginal("${Bot.MASCOT} Playing **${item.data.info.title}**.").queue()
                } else {
                    event.hook.editOriginal("${Bot.MASCOT} Added **${item.data.info.title}** to queue.").queue()
                }

                guildMusicManager.getQueue().add(item.data)
            }

            is LoadResult.SearchResult -> {
                if (guildMusicManager.getQueue().getQueue().isEmpty()) {
                    event.hook.editOriginal("${Bot.MASCOT} Playing **${item.data.tracks[0].info.title}**.").queue()
                } else {
                    event.hook.editOriginal("${Bot.MASCOT} Added **${item.data.tracks[0].info.title}** to queue.")
                        .queue()
                }

                guildMusicManager.getQueue().add(item.data.tracks[0])
            }

            is LoadResult.PlaylistLoaded -> {
                if (item.loadType == ResultStatus.SEARCH) {
                    if (guildMusicManager.getQueue().getQueue().isEmpty()) {
                        event.hook.editOriginal("${Bot.MASCOT} Playing **${item.data.tracks[0].info.title}**.").queue()
                    } else {
                        event.hook.editOriginal("${Bot.MASCOT} Added **${item.data.tracks[0].info.title}** to queue.")
                            .queue()
                    }

                    guildMusicManager.getQueue().add(item.data.tracks[0])
                } else {
                    event.hook.editOriginal("${Bot.MASCOT} Added **${item.data.tracks.size}** tracks to queue.").queue()

                    item.data.tracks.forEach { guildMusicManager.getQueue().add(it) }
                }
            }

            is LoadResult.NoMatches -> {
                if (tried.remains.isNotEmpty()) {
                    tried.provider = tried.remains.removeFirst()

                    load(tried, event, guildMusicManager)
                    return
                }

                event.hook.editOriginal("${Bot.MASCOT} No matches found.").queue()
            }

            is LoadResult.LoadFailed -> {
                if (tried.remains.isNotEmpty()) {
                    tried.provider = tried.remains.removeFirst()

                    load(tried, event, guildMusicManager)
                    return
                }

                event.hook.editOriginal("${Bot.MASCOT} Failed to load track(s).").queue()
            }
        }
    }

    private fun getSource(query: String): Source {
        if (!query.contains("http")) {
            return Source("ytsearch", query, mutableListOf("spsearch", "dzsearch", "scsearch"))
        }

        return Source(null, query, mutableListOf("ytsearch", "spsearch", "dzsearch", "scsearch"))
    }
}
