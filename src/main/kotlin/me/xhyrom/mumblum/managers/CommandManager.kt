package me.xhyrom.mumblum.managers

import me.xhyrom.mumblum.api.structs.Command
import me.xhyrom.mumblum.commands.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.interactions.commands.build.Commands

object CommandManager {
    private val commands: MutableList<Command> = ArrayList()

    fun registerCommands() {
        commands.add(Ping())
        commands.add(Play())
        commands.add(Nowplaying())
        commands.add(Stop())
        commands.add(Queue())
        commands.add(Loop())
        commands.add(Skip())
        commands.add(Volume())
        commands.add(Filter())
    }

    fun getCommand(name: String): Command? {
        return commands.firstOrNull { it.name == name }
    }

    fun registerCommandsDiscordAPI(jda: JDA) {
        jda.updateCommands().addCommands(
            commands.map {
                Commands.slash(it.name, it.description)
                    .addOptions(it.options!!)
            }
        ).queue()
    }
}