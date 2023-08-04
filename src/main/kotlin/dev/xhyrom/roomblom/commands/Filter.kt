package dev.xhyrom.roomblom.commands

import dev.arbjerg.lavalink.protocol.v4.*
import dev.schlaubi.lavakord.audio.player.*
import dev.xhyrom.roomblom.Bot
import dev.xhyrom.roomblom.api.structs.Command
import dev.xhyrom.roomblom.managers.VoteManager
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.Button

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
        val filter = event.getOption("filter")?.asString
            ?: return event.reply("${Bot.MASCOT} You must provide a filter.").setEphemeral(true)
                .queue()
        val guild = event.guild
            ?: return event.reply("${Bot.MASCOT} You must be in a guild to use this command.")
                .setEphemeral(true).queue()

        if (event.member?.voiceState?.channel == null) {
            return event.reply("${Bot.MASCOT} You must be in a voice channel to use this command.")
                .setEphemeral(true).queue()
        }

        if (event.member?.voiceState?.channel != event.guild?.selfMember?.voiceState?.channel) {
            return event.reply("${Bot.MASCOT} You must be in the same voice channel as the bot to use this command.")
                .setEphemeral(true).queue()
        }

        val musicManager = Bot.getLavaLinkManager().getGuildMusicManagerUnsafe(guild)
            ?: return event.reply("${Bot.MASCOT} The bot is not connected to a voice channel.")
                .setEphemeral(true).queue()

        event.deferReply().queue()

        VoteManager.hasVote(event.user.id).thenAccept { voted ->
            if (!voted) {
                event.hook
                    .editOriginal("${Bot.MASCOT} You must vote for the bot to use this command.")
                    .setActionRow(Button.link("https://top.gg/bot/1051248938709168199/vote", "Vote"))
                    .queue()
                return@thenAccept
            }

            musicManager.getCoroutineScope().launch {
                when (filter) {
                    "karaoke" -> {
                        musicManager.getPlayer().filters.karaoke {
                            level = 15.0f
                        }
                    }

                    "timescale" -> {
                        musicManager.getPlayer().filters.timescale {  }
                    }

                    "tremolo" -> {
                        musicManager.getPlayer().filters.tremolo {  }
                    }

                    "vibrato" -> {
                        musicManager.getPlayer().filters.vibrato { }
                    }

                    "rotation" -> {
                        musicManager.getPlayer().filters.rotation {  }
                    }

                    "distortion" -> {
                        musicManager.getPlayer().filters.distortion {  }
                    }

                    "channelmix" -> {
                        musicManager.getPlayer().filters.channelMix {  }
                    }

                    "lowpass" -> {
                        musicManager.getPlayer().filters.lowPass {}
                    }

                    "off" -> {
                        musicManager.getPlayer().resetFilters()
                    }
                }

                event.hook.editOriginal("${Bot.MASCOT} Filter set to $filter.").queue()
            }
        }
    }
}
