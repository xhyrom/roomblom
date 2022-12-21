package me.xhyrom.mumblum.commands

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import me.xhyrom.mumblum.Bot
import me.xhyrom.mumblum.api.structs.Command
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData
import java.awt.Color

class Queue : Command("queue", "Show the queue") {
    override fun execute(event: SlashCommandInteractionEvent) {
        val guild = event.guild ?: return event.reply("You must be in a guild to use this command").setEphemeral(true).queue()
        val musicManager = Bot.getLavaLinkManager().getGuildMusicManager(guild)
        val queue = musicManager.getQueue().getQueue()

        if (queue.isEmpty()) {
            event.reply("The queue is empty").queue()
            return
        }

        val page = getQueuePage(queue.toList(), 0)

        event.reply(
            MessageCreateData.fromEmbeds(buildEmbed(page, 0, queue.toList()))
        ).addActionRow(
            Button.primary("queue-previous-0", "Previous").withDisabled(true),
            Button.primary("queue-next-0", "Next").withDisabled(queue.size <= 10)
        ).queue()
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val guild = event.guild ?: return event.reply("You must be in a guild to use this command").setEphemeral(true).queue()
        val action = event.componentId.split("-")[1]
        var pageId = event.componentId.split("-")[2].toInt()

        val musicManager = Bot.getLavaLinkManager().getGuildMusicManager(guild)
        val queue = musicManager.getQueue().getQueue()

        when (action) {
            "next" -> {
                pageId += 1

                val page = getQueuePage(queue.toList(), pageId)
                event.editMessage(
                    MessageEditData.fromEmbeds(buildEmbed(page, pageId, queue.toList()))
                ).setActionRow(
                    Button.primary("queue-previous-${pageId}", "Previous").withDisabled(pageId == 0),
                    Button.primary("queue-next-${pageId}", "Next").withDisabled(getQueuePage(queue.toList(), pageId + 1).isEmpty())
                ).queue()
            }
            "previous" -> {
                pageId -= 1

                val page = getQueuePage(queue.toList(), pageId)
                event.editMessage(
                    MessageEditData.fromEmbeds(buildEmbed(page, pageId, queue.toList()))
                ).setActionRow(
                    Button.primary("queue-previous-${pageId}", "Previous").withDisabled(pageId == 0),
                    Button.primary("queue-next-${pageId}", "Next").withDisabled(getQueuePage(queue.toList(), pageId + 1).isEmpty())
                ).queue()
            }
        }
    }

    private fun getQueuePage(queue: List<AudioTrack>, page: Int): List<AudioTrack> {
        val start = page * 10
        val end = start + 10

        if (start >= queue.size || start > end) {
            return listOf()
        }

        return queue.subList(start, end.coerceAtMost(queue.size))
    }

    private fun buildEmbed(queue: List<AudioTrack>, page: Int, fullQueue: List<AudioTrack>): MessageEmbed {
        val embed = EmbedBuilder()
            .setTitle("Queue")
            .setColor(Color.decode("#fcba03"))
            .setDescription("```nim\n${
                queue.joinToString("\n") { 
                    "${fullQueue.indexOf(it) + 1}) ${
                        if (it.info.title.length > 60) it.info.title.slice(0..60)
                        else it.info.title
                    }" 
                }
            }```")
            .setFooter("Page ${page + 1} | ${fullQueue.size} tracks")

        return embed.build()
    }
}