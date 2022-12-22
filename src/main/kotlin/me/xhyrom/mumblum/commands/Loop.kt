package me.xhyrom.mumblum.commands

import me.xhyrom.mumblum.Bot
import me.xhyrom.mumblum.api.structs.Command
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class Loop : Command("loop", "Loop the current song") {
    override fun execute(event: SlashCommandInteractionEvent) {
        val guild = event.guild ?: return event.reply("You must be in a guild to use this command").setEphemeral(true).queue()

        if (event.member?.voiceState?.channel == null) {
            return event.reply("You must be in a voice channel to use this command").setEphemeral(true).queue()
        }

        if (event.member?.voiceState?.channel != event.guild?.selfMember?.voiceState?.channel) {
            return event.reply("You must be in the same voice channel as the bot to use this command").setEphemeral(true).queue()
        }

        val musicManager = Bot.getLavaLinkManager().getGuildMusicManager(guild)

        musicManager.setLoop(!musicManager.isLoop())

        event.reply("Looping is now ${if (musicManager.isLoop()) "enabled" else "disabled"}").queue()
    }
}