package dev.bogwalk.routes

import dev.bogwalk.models.Deck
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal fun customTestApp(
    block: suspend ApplicationTestBuilder.() -> Unit
) = testApplication {
    environment {
        config = ApplicationConfig("application-test.conf")
    }
    block()
}

internal fun cleanTestDatabase() = customTestApp {
    val client = customClient()
    val allDecks = client.get(Routes.ALL_DECKS).bodyAsText().toDataClass<List<Deck>>()

    for (deck in allDecks) {
        client.delete("${Routes.ALL_DECKS}/${deck.id}")
    }
}

internal fun ApplicationTestBuilder.customClient() = createClient {
    install(ContentNegotiation) {
        json()
    }
}

internal inline fun <reified R : Any> String.toDataClass() = Json.decodeFromString<R>(this)