package dev.xhyrom.roomblom.commands

import dev.xhyrom.roomblom.Bot
import dev.xhyrom.roomblom.api.structs.Command
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class Stop : Command("stop", "Stop the music") {
    override fun execute(event: SlashCommandInteractionEvent) {
        val guild = event.guild ?: return event.reply("${Bot.MASCOT} You must be in a guild to use this command.")
            .setEphemeral(true).queue()
        val voiceChannel = event.member?.voiceState?.channel?.asVoiceChannel()
            ?: return event.reply("${Bot.MASCOT} You must be in a voice channel to use this command.")
                .setEphemeral(true).queue()
        val musicManager = Bot.getLavaLinkManager().getGuildMusicManagerUnsafe(guild)
            ?: return event.reply("${Bot.MASCOT} The bot is not connected to a voice channel.").setEphemeral(true)
                .queue()

        if (event.guild?.selfMember?.voiceState?.channel != null && event.guild?.selfMember?.voiceState?.channel != voiceChannel) {
            return event.reply("${Bot.MASCOT} You must be in the same voice channel as the bot to use this command.")
                .setEphemeral(true).queue()
        }

        musicManager.getCoroutineScope().launch {
            musicManager.getLink().destroy()
            musicManager.destroy()
        }

        event.reply("${Bot.MASCOT} Music has been stopped.").queue()
    }
}