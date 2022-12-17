package me.xhyrom.mumblum.managers

import lavalink.client.io.jda.JdaLavalink
import me.xhyrom.mumblum.Bot
import net.dv8tion.jda.api.entities.Guild
import java.net.URI

class LavalinkManager {
    private val lavaLink: JdaLavalink = JdaLavalink(
        "1051248938709168199",
        1,
    ) { Bot.getInstanceUnsafe().getApi() }
    private val guilds = mutableMapOf<Long, GuildMusicManager>()

    init {
        lavaLink.addNode(
            "mumblum",
            URI.create(Bot.getInstanceUnsafe().getDotenv().get("LAVALINK_URI")),
            Bot.getInstanceUnsafe().getDotenv().get("LAVALINK_PASSWORD")
        )
    }

    fun getGuildMusicManager(guild: Guild): GuildMusicManager {
        return guilds[guild.idLong] ?: GuildMusicManager(guild).also { guilds[guild.idLong] = it }
    }

    fun getGuildMusicManagers(): MutableMap<Long, GuildMusicManager> {
        return guilds
    }

    fun getLavaLink(): JdaLavalink {
        return lavaLink
    }
}