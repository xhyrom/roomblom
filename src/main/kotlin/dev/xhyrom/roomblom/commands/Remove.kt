package dev.xhyrom.roomblom.commands

import dev.xhyrom.roomblom.Bot
import dev.xhyrom.roomblom.api.structs.Command
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class Remove : Command(
    "remove",
    "Remove song from queue",
    listOf(
        OptionData(
            OptionType.STRING,
            "song",
            "The song to remove",
            true,
            true
        )
    )
) {
    override fun execute(event: SlashCommandInteractionEvent) {
        val song = event.getOption("song")!!.asString

        val guildMusicManager = Bot.getLavaLinkManager().getGuildMusicManagerUnsafe(event.guild!!)
            ?: return event.reply("${Bot.MASCOT} The bot is not connected to a voice channel.").setEphemeral(true)
                .queue()

        val queue = guildMusicManager.getQueue().getQueue()

        val track = queue.find { it.info.title.contains(song) }
            ?: return event.reply("${Bot.MASCOT} Song not found.").setEphemeral(true).queue()

        queue.remove(track)

        event.reply("${Bot.MASCOT} Removed **$song** from queue.").queue()
    }

    override fun onAutoComplete(event: CommandAutoCompleteInteractionEvent): List<net.dv8tion.jda.api.interactions.commands.Command.Choice> {
        val guild = event.guild ?: return emptyList()

        val musicManager = Bot.getLavaLinkManager().getGuildMusicManagerUnsafe(guild)
            ?: return emptyList()

        val queue = musicManager.getQueue().getQueue()

        val query = event.getOption("song")!!.asString

        return if (query.isEmpty())
            queue.map {
                net.dv8tion.jda.api.interactions.commands.Command.Choice(
                    "${it.info.title} - ${it.info.author}".take(
                        100
                    ), it.info.title.take(100)
                )
            }
        else queue
            .filter { it.info.title.contains(query, true) }
            .map {
                net.dv8tion.jda.api.interactions.commands.Command.Choice(
                    "${it.info.title} - ${it.info.author}".take(
                        100
                    ), it.info.title.take(100)
                )
            }
            .take(25)
    }
}