package dev.xhyrom.roomblom.managers

import dev.xhyrom.roomblom.commands.*
import dev.xhyrom.roomblom.commands.moderation.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.interactions.commands.build.Commands

object CommandManager {
    private val commands: MutableList<dev.xhyrom.roomblom.api.structs.Command> = ArrayList()

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
        commands.add(Remove())
        commands.add(Info())

        // I will remove them - just for discord :)
        commands.add(Ban())
        commands.add(Kick())
    }

    fun getCommand(name: String): dev.xhyrom.roomblom.api.structs.Command? {
        return commands.firstOrNull { it.name == name }
    }

    fun registerCommandsDiscordAPI(jda: JDA) {
        jda.updateCommands().addCommands(
            commands.map {
                Commands.slash(it.name, it.description)
                    .addOptions(it.options!!)
                    .setDefaultPermissions(it.defaultMemberPermissions)
            }
        ).queue()
    }
}