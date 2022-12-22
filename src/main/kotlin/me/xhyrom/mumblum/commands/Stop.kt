package me.xhyrom.mumblum.commands

import me.xhyrom.mumblum.Bot
import me.xhyrom.mumblum.api.structs.Command
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class Stop : Command("stop", "Stop the music") {
    override fun execute(event: SlashCommandInteractionEvent) {
        val guild = event.guild ?: return event.reply("You must be in a guild to use this command").setEphemeral(true).queue()
        val voiceChannel = event.member?.voiceState?.channel?.asVoiceChannel()
            ?: return event.hook.editOriginal("You must be in a voice channel to use this command").queue()
        val musicManager = Bot.getLavaLinkManager().getGuildMusicManagerUnsafe(guild)
            ?: return event.reply("The bot is not connected to a voice channel").setEphemeral(true).queue()

        if (event.guild?.selfMember?.voiceState?.channel != null && event.guild?.selfMember?.voiceState?.channel != voiceChannel) {
            return event.reply("You must be in the same voice channel as the bot to use this command").setEphemeral(true).queue()
        }

        musicManager.getLink().destroy()
        musicManager.destroy()

        event.reply("Stopped the music").queue()
    }
}