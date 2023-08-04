package dev.xhyrom.roomblom.commands.moderation

import dev.xhyrom.roomblom.Bot
import dev.xhyrom.roomblom.api.structs.Command
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class Kick : Command(
    "kick",
    "Kick a user",
    listOf(
        OptionData(
            OptionType.USER,
            "user",
            "The user to kick",
            true
        ),
        OptionData(
            OptionType.STRING,
            "reason",
            "The reason for the kick",
            false
        ),
    ),
    DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS),
) {
    override fun execute(event: SlashCommandInteractionEvent) {
        val user = event.getOption("user")!!.asUser
        val reason = event.getOption("reason")?.asString ?: "No reason provided"

        user.openPrivateChannel().queue {
            try {
                it.sendMessage("${Bot.MASCOT} You have been kicked from ${event.guild!!.name} for $reason").queue()
            } catch (_: Exception) {
            }

            event.guild!!.kick(user).reason("${event.user.name} - $reason").queue()

            event.reply("${Bot.MASCOT} Kicked ${user.name} for $reason").queue()
        }
    }
}