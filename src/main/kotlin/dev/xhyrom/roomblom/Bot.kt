package dev.xhyrom.roomblom

import dev.schlaubi.lavakord.jda.LShardManager
import dev.schlaubi.lavakord.jda.buildWithLavakord
import io.github.cdimascio.dotenv.Dotenv
import dev.xhyrom.roomblom.listeners.GuildListener
import dev.xhyrom.roomblom.listeners.InteractionListener
import dev.xhyrom.roomblom.listeners.ReadyListener
import dev.xhyrom.roomblom.managers.CommandManager
import dev.xhyrom.roomblom.managers.LavalinkManager
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import org.discordbots.api.client.DiscordBotListAPI
import redis.clients.jedis.JedisPooled

object Bot {
    const val MASCOT = "<:roomblom:1060621623188787340>"

    private var dotenv: Dotenv = Dotenv.load()
    private var shardManager: LShardManager? = null
    private var discordBotListApi: DiscordBotListAPI? = null
    private var lavaLinkManager: LavalinkManager? = null
    private var redis: JedisPooled? = null

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            shardManager = DefaultShardManagerBuilder.createDefault(dotenv.get("BOT_TOKEN"))
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .addEventListeners(InteractionListener())
                .addEventListeners(GuildListener())
                .addEventListeners(ReadyListener())
                .buildWithLavakord()

            discordBotListApi = DiscordBotListAPI.Builder()
                .token(dotenv.get("TOPGG_TOKEN"))
                .botId(dotenv.get("BOT_CLIENT_ID"))
                .build()

            redis = JedisPooled(
                dotenv.get("REDIS_HOST"),
                dotenv.get("REDIS_PORT").toInt(),
                Bot.dotenv.get("REDIS_USERNAME"),
                Bot.dotenv.get("REDIS_PASSWORD")
            )

            lavaLinkManager = LavalinkManager()
            CommandManager.registerCommands()
        }
    }

    fun getDotenv(): Dotenv {
        return dotenv
    }

    fun getLShardManager(): LShardManager {
        return shardManager!!
    }

    fun getShardManager(): ShardManager {
        return shardManager!!.shardManager
    }

    fun getDiscordBotListApi(): DiscordBotListAPI {
        return discordBotListApi!!
    }

    fun getLavaLinkManager(): LavalinkManager {
        return lavaLinkManager!!
    }

    fun getRedis(): JedisPooled {
        return redis!!
    }
}