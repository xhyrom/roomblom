package dev.xhyrom.roomblom.commands.moderation

import dev.xhyrom.roomblom.Bot
import dev.xhyrom.roomblom.api.structs.Command
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.util.concurrent.TimeUnit

class Ban : Command(
    "ban",
    "Ban a user",
    listOf(
        OptionData(
            OptionType.USER,
            "user",
            "The user to ban",
            true
        ),
        OptionData(
            OptionType.STRING,
            "reason",
            "The reason for the ban",
            false
        ),
        OptionData(
            OptionType.INTEGER,
            "days",
            "The number of days of messages to delete",
            false
        ).setMinValue(1).setMaxValue(7)
    ),
    DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS),
) {
    override fun execute(event: SlashCommandInteractionEvent) {
        val user = event.getOption("user")!!.asUser
        val reason = event.getOption("reason")?.asString ?: "No reason provided"
        val days = event.getOption("days")?.asInt ?: 0

        user.openPrivateChannel().queue {
            try {
                it.sendMessage("${Bot.MASCOT} You have been banned from ${event.guild!!.name} for $reason").queue()
            } catch (_: Exception) {
            }

            event.guild!!.ban(user, days, TimeUnit.DAYS).reason(reason).queue()

            event.reply("${Bot.MASCOT} Banned ${user.asTag} for $reason").queue()
        }
    }
}