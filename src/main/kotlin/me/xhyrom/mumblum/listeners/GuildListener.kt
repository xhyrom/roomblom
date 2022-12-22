package me.xhyrom.mumblum.listeners

import me.xhyrom.mumblum.Bot
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class GuildListener : ListenerAdapter() {
    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        if (event.guild.selfMember.voiceState?.channel == null) {
            val musicManager = Bot.getLavaLinkManager().getGuildMusicManagerUnsafe(event.guild)
            musicManager?.getLink()?.destroy()
            musicManager?.destroy()
        }
    }
}