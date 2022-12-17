package me.xhyrom.mumblum.listeners

import me.xhyrom.mumblum.Bot
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.Command
import java.util.stream.Stream

class InteractionListener : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val command = event.name
        val commandManager = Bot.getInstanceUnsafe().getCommandManager()
        commandManager.getCommand(command)?.execute(event)
    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        val command = event.name
        val commandManager = Bot.getInstanceUnsafe().getCommandManager()

        event.replyChoiceStrings(commandManager.getCommand(command)!!.onAutoComplete(event)).queue()
    }
}