package me.xhyrom.mumblum.listeners

import me.xhyrom.mumblum.Bot
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.Command
import java.util.stream.Stream

class InteractionListener : ListenerAdapter() {
    private val blacklist = listOf("")

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (blacklist.contains(event.user.id)) {
            event.reply("You are blacklisted from using this bot. hehe").setEphemeral(false).queue()
            return
        }

        val command = event.name
        val commandManager = Bot.getInstanceUnsafe().getCommandManager()
        commandManager.getCommand(command)?.execute(event)
    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        val command = event.name
        val commandManager = Bot.getInstanceUnsafe().getCommandManager()

        event.replyChoiceStrings(commandManager.getCommand(command)!!.onAutoComplete(event)).queue()
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (blacklist.contains(event.user.id)) {
            event.reply("You are blacklisted from using this bot. hehe").setEphemeral(false).queue()
            return
        }

        val command = event.componentId.split("-")[0]
        val commandManager = Bot.getInstanceUnsafe().getCommandManager()

        commandManager.getCommand(command)!!.onButtonInteraction(event)
    }
}