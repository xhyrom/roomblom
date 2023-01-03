package me.xhyrom.mumblum

import io.github.cdimascio.dotenv.Dotenv
import me.xhyrom.mumblum.listeners.GuildListener
import me.xhyrom.mumblum.listeners.InteractionListener
import me.xhyrom.mumblum.listeners.ReadyListener
import me.xhyrom.mumblum.managers.CommandManager
import me.xhyrom.mumblum.managers.LavalinkManager
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import org.discordbots.api.client.DiscordBotListAPI

object Bot {
    const val MASCOT = "<:mumblum:1056308754490077294>"

    private var dotenv: Dotenv = Dotenv.load()
    private var shardManager: ShardManager? = null
    private var discordBotListApi: DiscordBotListAPI? = null
    private var lavaLinkManager: LavalinkManager? = null

    @JvmStatic
    fun main(args: Array<String>) {
        lavaLinkManager = LavalinkManager()

        shardManager = DefaultShardManagerBuilder.createDefault(dotenv.get("BOT_TOKEN"))
            .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
            .addEventListeners(InteractionListener())
            .addEventListeners(GuildListener())
            .addEventListeners(ReadyListener())
            .setVoiceDispatchInterceptor(lavaLinkManager!!.getLavaLink().voiceInterceptor)
            .build()

        discordBotListApi = DiscordBotListAPI.Builder()
            .token(dotenv.get("TOPGG_TOKEN"))
            .botId(dotenv.get("BOT_CLIENT_ID"))
            .build()

        CommandManager.registerCommands()
    }

    fun getDotenv(): Dotenv {
        return dotenv
    }

    fun getShardManager(): ShardManager {
        return shardManager!!
    }

    fun getDiscordBotListApi(): DiscordBotListAPI {
        return discordBotListApi!!
    }

    fun getLavaLinkManager(): LavalinkManager {
        return lavaLinkManager!!
    }
}