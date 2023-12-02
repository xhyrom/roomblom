package dev.xhyrom.roomblom.managers

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object WebhookSender {
    fun sendWebhook(webhookUrl: String, content: String) {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(webhookUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{\"content\":\"${content}\"}"))
            .build()

        client.send(request, HttpResponse.BodyHandlers.ofString())
    }
}