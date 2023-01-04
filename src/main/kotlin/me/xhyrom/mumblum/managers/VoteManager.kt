package me.xhyrom.mumblum.managers

import me.xhyrom.mumblum.Bot
import java.util.concurrent.CompletableFuture

object VoteManager {
    fun hasVote(userId: String): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val cache = Bot.getRedis().get("mumblum:vote:$userId").toBooleanStrictOrNull()
            if (cache != null) {
                return@supplyAsync cache
            }

            val voted = Bot.getDiscordBotListApi().hasVoted(userId).toCompletableFuture().get()
            Bot.getRedis().setex("mumblum:vote:$userId", 43000, voted.toString())

            return@supplyAsync voted
        }
    }
}