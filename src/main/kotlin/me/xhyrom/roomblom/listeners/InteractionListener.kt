package me.xhyrom.roomblom.listeners

import me.xhyrom.roomblom.managers.CommandManager
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class InteractionListener : ListenerAdapter() {
    private val blacklist = listOf("")

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (blacklist.contains(event.user.id)) {
            event.reply("You are blacklisted from using this bot. hehe").setEphemeral(false).queue()
            return
        }

        val command = event.name
        CommandManager.getCommand(command)?.execute(event)
    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        val command = event.name

        event.replyChoices(CommandManager.getCommand(command)!!.onAutoComplete(event)).queue()
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (blacklist.contains(event.user.id)) {
            event.reply("You are blacklisted from using this bot. hehe").setEphemeral(false).queue()
            return
        }

        val command = event.componentId.split("-")[0]

        CommandManager.getCommand(command)!!.onButtonInteraction(event)
    }
}