package dev.xhyrom.roomblom.commands

import dev.xhyrom.roomblom.Bot
import dev.xhyrom.roomblom.api.structs.Command
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class Ping : Command("ping", "pong 🏓") {
    override fun execute(event: SlashCommandInteractionEvent) {
        event.reply("${Bot.MASCOT} Gateway: ${event.jda.gatewayPing}ms | Rest: ${event.jda.restPing.complete()}ms | Shard: ${event.jda.shardInfo.shardId}")
            .queue()
    }
}