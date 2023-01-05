package me.xhyrom.roomblom.managers

import java.util.concurrent.CompletableFuture

object VoteManager {
    fun hasVote(userId: String): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            /*val cache = Bot.getRedis().get("mumblum:vote:$userId")
            if (cache != null) {
                return@supplyAsync cache.toBooleanStrict()
            }

            return@supplyAsync false*/
            return@supplyAsync true
        }
    }
}