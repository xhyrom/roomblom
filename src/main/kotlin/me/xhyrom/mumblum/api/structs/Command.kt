package me.xhyrom.mumblum.api.structs

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData

abstract class Command(
    val name: String,
    val description: String,
    val options: List<OptionData>? = listOf()
) {
    abstract fun execute(event: SlashCommandInteractionEvent)

    open fun onAutoComplete(event: CommandAutoCompleteInteractionEvent): List<net.dv8tion.jda.api.interactions.commands.Command.Choice> {
        return listOf()
    }

    open fun onButtonInteraction(event: ButtonInteractionEvent) {

    }
}