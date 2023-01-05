package me.xhyrom.roomblom.commands

import me.xhyrom.roomblom.Bot
import me.xhyrom.roomblom.api.structs.Command
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class Ping : Command("ping", "pong 🏓") {
    override fun execute(event: SlashCommandInteractionEvent) {
        event.reply("${Bot.MASCOT} Gateway: ${event.jda.gatewayPing}ms | Rest: ${event.jda.restPing.complete()}ms | Shard: ${event.jda.shardInfo.shardId}").queue()
    }
}