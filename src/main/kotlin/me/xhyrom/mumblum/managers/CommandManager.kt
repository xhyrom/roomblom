package me.xhyrom.mumblum.managers

import me.xhyrom.mumblum.Bot
import me.xhyrom.mumblum.api.structs.Command
import me.xhyrom.mumblum.commands.*
import net.dv8tion.jda.api.interactions.commands.build.Commands

class CommandManager {
    private val commands: MutableList<Command> = ArrayList()

    init {
        commands.add(Ping())
        commands.add(Play())
        commands.add(Nowplaying())
        commands.add(Stop())
        commands.add(Queue())

        registerCommands()
    }

    fun getCommand(name: String): Command? {
        return commands.firstOrNull { it.name == name }
    }

    private fun registerCommands() {
        Bot.getInstanceUnsafe().getApi().updateCommands().addCommands(
            commands.map {
                Commands.slash(it.name, it.description)
                    .addOptions(it.options!!)
            }
        ).queue()
    }
}