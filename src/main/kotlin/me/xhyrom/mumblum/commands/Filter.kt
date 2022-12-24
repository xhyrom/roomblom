package me.xhyrom.mumblum.commands

import lavalink.client.io.filters.*
import me.xhyrom.mumblum.Bot
import me.xhyrom.mumblum.api.structs.Command
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class Filter : Command(
    "filter",
    "Filter the music",
    listOf(
        OptionData(
            OptionType.STRING,
            "filter",
            "The filter to set",
            true
        ).addChoices(listOf(
            "karaoke",
            "timescale",
            "tremolo",
            "vibrato",
            "rotation",
            "distortion",
            "channelmix",
            "lowpass",
            "off",
        ).map { net.dv8tion.jda.api.interactions.commands.Command.Choice(it, it) })
    )
) {
    override fun execute(event: SlashCommandInteractionEvent) {
        val filter = event.getOption("filter")?.asString ?: return event.reply("${Bot.MASCOT} You must provide a filter.").setEphemeral(true).queue()
        val guild = event.guild ?: return event.reply("${Bot.MASCOT} You must be in a guild to use this command.").setEphemeral(true).queue()

        if (event.member?.voiceState?.channel == null) {
            return event.reply("${Bot.MASCOT} You must be in a voice channel to use this command.").setEphemeral(true).queue()
        }

        if (event.member?.voiceState?.channel != event.guild?.selfMember?.voiceState?.channel) {
            return event.reply("${Bot.MASCOT} You must be in the same voice channel as the bot to use this command.").setEphemeral(true).queue()
        }

        val musicManager = Bot.getLavaLinkManager().getGuildMusicManagerUnsafe(guild)
            ?: return event.reply("${Bot.MASCOT} The bot is not connected to a voice channel.").setEphemeral(true).queue()

        when (filter) {
            "karaoke" -> {
                musicManager.getPlayer().filters.karaoke = Karaoke().setLevel(15.0F)
            }
            "timescale" -> {
                musicManager.getPlayer().filters.timescale = Timescale()
            }
            "tremolo" -> {
                musicManager.getPlayer().filters.tremolo = Tremolo()
            }
            "vibrato" -> {
                musicManager.getPlayer().filters.vibrato = Vibrato()
            }
            "rotation" -> {
                musicManager.getPlayer().filters.rotation = Rotation()
            }
            "distortion" -> {
                musicManager.getPlayer().filters.distortion = Distortion()
            }
            "channelmix" -> {
                musicManager.getPlayer().filters.channelMix = ChannelMix()
            }
            "lowpass" -> {
                musicManager.getPlayer().filters.lowPass = LowPass()
            }
            "off" -> {
                musicManager.getPlayer().filters.clear()
            }
        }

        event.reply("${Bot.MASCOT} Filter set to $filter.").queue()
    }
}
