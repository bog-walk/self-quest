package dev.bogwalk.routes

import dev.bogwalk.models.Deck
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.junit.AfterClass
import org.junit.BeforeClass
import kotlin.test.*

internal class DeckRoutesTest {
    companion object {
        @JvmStatic
        @BeforeClass
        fun setUp() {
            cleanTestDatabase()
        }

        @JvmStatic
        @AfterClass
        fun tearDown() {
            cleanTestDatabase()
        }
    }

    @Test
    fun `Deck routes produce expected database entities`() = customTestApp {
        val client = customClient()
        // GET request to empty database returns an empty list with 200 OK
        var response = client.get(Routes.ALL_DECKS)

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue { response.bodyAsText().toDataClass<List<Deck>>().isEmpty() }

        // POST adds an empty Deck with 201 Created
        val name = "Test Deck"
        response = client.post(Routes.ALL_DECKS) {
            contentType(ContentType.Application.Json)
            setBody(Deck(1, name, 0))
        }
        val deck = response.body<Deck>()

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(name, deck.name)

        client.post(Routes.ALL_DECKS) {
            contentType(ContentType.Application.Json)
            setBody(Deck(2, "Other $name", 0))
        }
        // GET returns list of created Decks
        response = client.get(Routes.ALL_DECKS)
        var allDecks = response.bodyAsText().toDataClass<List<Deck>>()

        assertEquals(2, allDecks.size)

        // PUT by id updates existing Deck
        response = client.put("${Routes.ALL_DECKS}/${deck.id}") {
            contentType(ContentType.Application.Json)
            setBody(deck.copy(name = "OG $name"))
        }

        assertEquals(HttpStatusCode.OK, response.status)

        // GET by id returns updated Deck
        response = client.get("${Routes.ALL_DECKS}/${deck.id}")
        val updated = response.body<Deck>()

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("OG $name", updated.name)
        assertNotEquals(deck, updated)

        // DELETE by id deletes existing Deck
        response = client.delete("${Routes.ALL_DECKS}/${deck.id}")

        assertEquals(HttpStatusCode.OK, response.status)

        response = client.delete("${Routes.ALL_DECKS}/${deck.id}")
        // otherwise gives 404 Not Found
        assertEquals(HttpStatusCode.NotFound, response.status)

        response = client.get(Routes.ALL_DECKS)
        allDecks = response.bodyAsText().toDataClass()

        assertEquals(1, allDecks.size)
    }
}