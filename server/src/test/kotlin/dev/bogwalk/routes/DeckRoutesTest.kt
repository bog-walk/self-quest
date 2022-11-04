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
            setBody(name)
        }
        val deckId = response.body<Int>()

        assertEquals(HttpStatusCode.Created, response.status)

        // GET by id returns a Deck
        response = client.get("${Routes.ALL_DECKS}/$deckId")
        val firstDeck = response.body<Deck>()

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(name, firstDeck.name)

        // POST a second Deck
        response = client.post(Routes.ALL_DECKS) {
            contentType(ContentType.Application.Json)
            setBody("Other $name")
        }

        assertEquals(HttpStatusCode.Created, response.status)

        // GET returns list of created Decks with most recently created/modified first
        response = client.get(Routes.ALL_DECKS)
        var allDecks = response.bodyAsText().toDataClass<List<Deck>>()

        assertEquals(2, allDecks.size)
        assertEquals(firstDeck, allDecks.last())

        // PUT by id updates existing Deck
        response = client.put("${Routes.ALL_DECKS}/$deckId") {
            contentType(ContentType.Application.Json)
            setBody(firstDeck.copy(name = "OG $name"))
        }

        assertEquals(HttpStatusCode.OK, response.status)

        // GET returns list of created Decks with most recently created/modified first
        response = client.get(Routes.ALL_DECKS)
        allDecks = response.bodyAsText().toDataClass()

        assertEquals(2, allDecks.size)

        // GET by id returns updated Deck
        response = client.get("${Routes.ALL_DECKS}/$deckId")
        val updated = response.body<Deck>()

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("OG $name", updated.name)
        assertNotEquals(firstDeck, updated)
        assertEquals(allDecks.first(), updated)

        // DELETE by id deletes existing Deck
        response = client.delete("${Routes.ALL_DECKS}/$deckId")

        assertEquals(HttpStatusCode.OK, response.status)

        response = client.delete("${Routes.ALL_DECKS}/$deckId")
        // otherwise gives 404 Not Found
        assertEquals(HttpStatusCode.NotFound, response.status)

        response = client.get(Routes.ALL_DECKS)
        allDecks = response.bodyAsText().toDataClass()

        assertEquals(1, allDecks.size)
    }
}