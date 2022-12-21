package me.xhyrom.mumblum.listeners

import me.xhyrom.mumblum.managers.CommandManager
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ReadyListener : ListenerAdapter() {
    var registered = false

    override fun onReady(event: ReadyEvent) {
        if (!registered) {
            CommandManager.registerCommandsDiscordAPI(event.jda)
            registered = true
        }
    }
}