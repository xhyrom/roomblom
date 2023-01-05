package me.xhyrom.roomblom.commands

import me.xhyrom.roomblom.Bot
import me.xhyrom.roomblom.api.structs.Command
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class Info : Command("info", "Get information about the bot") {
    override fun execute(event: SlashCommandInteractionEvent) {
        event.reply(
            """
${Bot.MASCOT} **Mumblum** is a music bot written in Kotlin.
       **Source code**: <https://github.com/xHyroM/mumblum>

            """
        ).addEmbeds(listOf(
            EmbedBuilder()
                .setTitle("Metrics")
                .addField(
                    "Guilds",
                    "```${Bot.getShardManager().shards.fold(0) { acc, jda -> acc + jda.guilds.size }}```",
                    true
                )
                .addField(
                    "Shards",
                    "```${Bot.getShardManager().shardsTotal} / ${Bot.getShardManager().shards.size} online```",
                    true
                )
                .addField(
                    "Voice connections",
                    "```${Bot.getShardManager().shards.fold(0) { acc, jda -> acc + jda.guilds.fold(0) { acc2, guild -> acc2 + if (Bot.getLavaLinkManager().getGuildMusicManagerUnsafe(guild) != null) 1 else 0 } }}```",
                    true
                )
                .build()
        )).setEphemeral(true).queue()
    }
}