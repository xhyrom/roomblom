package dev.xhyrom.roomblom.commands

import dev.xhyrom.roomblom.Bot
import dev.xhyrom.roomblom.api.structs.Command
import dev.xhyrom.roomblom.managers.VoteManager
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.Button

class Volume : Command(
    "volume",
    "Change the volume of the current song",
    listOf(
        OptionData(
            OptionType.INTEGER,
            "volume",
            "The volume to set",
            true
        ).setMaxValue(100).setMinValue(1)
    )
) {
    override fun execute(event: SlashCommandInteractionEvent) {
        val voiceChannel = event.member?.voiceState?.channel?.asVoiceChannel()
            ?: return event.reply("${Bot.MASCOT} You must be in a voice channel to use this command.")
                .setEphemeral(true).queue()
        if (event.guild?.selfMember?.voiceState?.channel != null && event.guild?.selfMember?.voiceState?.channel != voiceChannel) {
            return event.reply("${Bot.MASCOT} You must be in the same voice channel as the bot to use this command.")
                .setEphemeral(true).queue()
        }

        val guildMusicManager = Bot.getLavaLinkManager().getGuildMusicManagerUnsafe(voiceChannel.guild)
            ?: return event.reply("${Bot.MASCOT} The bot is not connected to a voice channel.").setEphemeral(true)
                .queue()

        event.deferReply().queue()

        VoteManager.hasVote(event.user.id).thenAccept { voted ->
            if (!voted) {
                event.hook
                    .editOriginal("${Bot.MASCOT} You must vote for the bot to use this command.")
                    .setActionRow(Button.link("https://top.gg/bot/1051248938709168199/vote", "Vote"))
                    .queue()
                return@thenAccept
            }

            guildMusicManager.getPlayer().filters.volume = event.getOption("volume")!!.asInt.toFloat()

            event.hook.editOriginal("${Bot.MASCOT} Set the volume to **${event.getOption("volume")!!.asInt}**.").queue()
        }
    }
}