package me.xhyrom.mumblum.listeners

import me.xhyrom.mumblum.Bot
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class GuildListener : ListenerAdapter() {
    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        if (event.channelLeft != null) {
            if (!event.oldValue!!.members.contains(event.guild.selfMember)) return

            if (!event.channelLeft!!.members.contains(event.guild.selfMember)) {
                val musicManager = Bot.getInstanceUnsafe().getLavaLinkManager().getGuildMusicManager(event.guild)
                musicManager.getLink().destroy()
                musicManager.destroy()
            }
        }
    }
}