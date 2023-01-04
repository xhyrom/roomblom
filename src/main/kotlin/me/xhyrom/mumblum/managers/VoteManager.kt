package me.xhyrom.mumblum.managers

import me.xhyrom.mumblum.Bot
import java.util.concurrent.CompletableFuture

object VoteManager {
    fun hasVote(userId: String): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val cache = Bot.getRedis().get("mumblum:vote:$userId")
            if (cache != null) {
                return@supplyAsync cache.toBooleanStrict()
            }

            return@supplyAsync false
        }
    }
}