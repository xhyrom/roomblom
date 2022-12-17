package me.xhyrom.mumblum.commands

import me.xhyrom.mumblum.Bot
import me.xhyrom.mumblum.api.structs.Command
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.awt.Color

class Nowplaying : Command("nowplaying", "Show the current song") {
    override fun execute(event: SlashCommandInteractionEvent) {
        val guild = event.guild ?: return event.reply("You must be in a guild to use this command").setEphemeral(true).queue()
        val guildMusicManager = Bot.getInstanceUnsafe().getLavaLinkManager().getGuildMusicManager(guild)
        val track = guildMusicManager.getPlayer().playingTrack
            ?: return event.reply("No song is currently playing").queue()

        val trackPosition = guildMusicManager.getPlayer().trackPosition
        val progress = trackPosition.toDouble() / track.duration.toDouble()
        val bar = "▬".repeat((progress * 10).toInt()) + "🔘" + "▬".repeat((10 - progress * 10).toInt())

        val embed = EmbedBuilder()
            .setTitle(track.info.title)
            .setThumbnail("https://i.ytimg.com/vi/${track.info.identifier}/hqdefault.jpg")

        if (track.info.isStream) embed.addField("Duration", ":red_circle: **LIVE**", false)
        else embed.addField("Duration", "$bar (${formatTime(trackPosition)}/${formatTime(track.info.length)})", false)

        embed.addField("Author", track.info.author, true)
        embed.setColor(Color.decode("#fcba03"))

        event.replyEmbeds(embed.build()).queue()
    }

    private fun formatTime(millis: Long): String {
        val days = millis / 1000 / 60 / 60 / 24
        val hours = millis / 1000 / 60 / 60 % 24
        val minutes = millis / 1000 / 60 % 60
        val seconds = millis / 1000 % 60
        return if (days > 0) {
            String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
        } else if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else if (minutes > 0) {
            String.format("%02d:%02d", minutes, seconds)
        } else {
            String.format("%02d", seconds)
        }
    }
}