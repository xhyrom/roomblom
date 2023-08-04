package dev.xhyrom.roomblom.managers

import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.jda.lavakord
import dev.xhyrom.roomblom.Bot
import net.dv8tion.jda.api.entities.Guild

class LavalinkManager {
    private val guilds = mutableMapOf<Long, GuildMusicManager>()

    init {
        Bot.getLShardManager().lavakord.addNode(
            Bot.getDotenv().get("LAVALINK_URI"),
            Bot.getDotenv().get("LAVALINK_PASSWORD")
        )
    }

    fun getGuildMusicManager(guild: Guild): GuildMusicManager {
        return guilds[guild.idLong] ?: GuildMusicManager(guild).also { guilds[guild.idLong] = it }
    }

    fun getGuildMusicManagerUnsafe(guild: Guild): GuildMusicManager? {
        return guilds[guild.idLong]
    }

    fun getGuildMusicManagers(): MutableMap<Long, GuildMusicManager> {
        return guilds
    }

    fun getLavaLink(): LavaKord {
        return Bot.getLShardManager().lavakord;
    }
}