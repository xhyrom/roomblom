package me.xhyrom.mumblum.commands

import me.xhyrom.mumblum.Bot
import me.xhyrom.mumblum.api.structs.Command
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class Stop : Command("stop", "Stop the music") {
    override fun execute(event: SlashCommandInteractionEvent) {
        val guild = event.guild ?: return event.reply("You must be in a guild to use this command").setEphemeral(true).queue()
        val musicManager = Bot.getLavaLinkManager().getGuildMusicManager(guild)

        musicManager.getLink().destroy()
        musicManager.destroy()

        event.reply("Stopped the music").queue()
    }
}