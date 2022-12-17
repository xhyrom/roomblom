package me.xhyrom.mumblum

import io.github.cdimascio.dotenv.Dotenv
import me.xhyrom.mumblum.listeners.InteractionListener
import me.xhyrom.mumblum.managers.CommandManager
import me.xhyrom.mumblum.managers.LavalinkManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder

class Bot {
    companion object {
        private var instance: Bot? = null

        fun getInstance(): Bot? {
            return instance
        }

        fun getInstanceUnsafe(): Bot {
            return instance!!
        }

        @JvmStatic
        fun main(args: Array<String>) {
            Bot()
        }
    }

    private var dotenv: Dotenv = Dotenv.load()
    private var api: JDA? = null
    private var commandManager: CommandManager? = null
    private var lavaLinkManager: LavalinkManager? = null

    init {
        instance = this

        lavaLinkManager = LavalinkManager()

        val builder = JDABuilder.createDefault(dotenv.get("BOT_TOKEN"))
            .addEventListeners(InteractionListener())
            .setVoiceDispatchInterceptor(lavaLinkManager!!.getLavaLink().voiceInterceptor)

        api = builder.build()

        commandManager = CommandManager()
    }

    fun getDotenv(): Dotenv {
        return dotenv
    }

    fun getApi(): JDA {
        return api!!
    }

    fun getCommandManager(): CommandManager {
        return commandManager!!
    }

    fun getLavaLinkManager(): LavalinkManager {
        return lavaLinkManager!!
    }
}