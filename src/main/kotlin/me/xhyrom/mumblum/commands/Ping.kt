package me.xhyrom.mumblum.commands

import me.xhyrom.mumblum.api.structs.Command
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class Ping : Command("ping", "pong 🏓") {
    override fun execute(event: SlashCommandInteractionEvent) {
        event.reply("Gateway: ${event.jda.gatewayPing}ms | Rest: ${event.jda.restPing.complete()}ms | Shard: ${event.jda.shardInfo.shardId}").queue()
    }
}