package dev.bogwalk.routes

import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.junit.AfterClass
import org.junit.BeforeClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

internal class QuestionRoutesTest {
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
    fun `Question routes produce expected database entities`() = customTestApp {
        val client = customClient()
        // inserted Deck is initially empty
        val deck = client.post(Routes.ALL_DECKS) {
            contentType(ContentType.Application.Json)
            setBody(Deck(1, "Test Deck", 0))
        }.body<Deck>()

        assertTrue {
            client.get("${Routes.ALL_DECKS}/${deck.id}/${Routes.ALL_QUESTIONS}")
                .bodyAsText()
                .toDataClass<List<Question>>()
                .isEmpty()
        }

        // POST adds a new Question to an existing Deck with 201 Created
        val content = "Fake question"
        var response = client.post("${Routes.ALL_DECKS}/${deck.id}/${Routes.ALL_QUESTIONS}") {
            contentType(ContentType.Application.Json)
            setBody(Question(
                1, "$content 1", "A", "B", "C", "D", "A"
            ))
        }
        val question = response.body<Question>()

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("A", question.expectedAnswer)

        client.post("${Routes.ALL_DECKS}/${deck.id}/${Routes.ALL_QUESTIONS}") {
            contentType(ContentType.Application.Json)
            setBody(Question(
                2, "$content 2", "A", "B", "C", "D", "C"
            ))
        }
        // GET returns list of created Questions for Deck by id
        response = client.get("${Routes.ALL_DECKS}/${deck.id}/${Routes.ALL_QUESTIONS}")
        var allQuestions = response.bodyAsText().toDataClass<List<Question>>()

        assertEquals(2, allQuestions.size)

        // PUT by id updates existing Question
        response = client.put("${Routes.ALL_DECKS}/${deck.id}/${Routes.ALL_QUESTIONS}/${question.id}") {
            contentType(ContentType.Application.Json)
            setBody(question.copy(content = "$content A"))
        }

        assertEquals(HttpStatusCode.OK, response.status)

        // GET by id returns updated Question
        response = client.get("${Routes.ALL_DECKS}/${deck.id}/${Routes.ALL_QUESTIONS}/${question.id}")
        val updated = response.body<Question>()

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("$content A", updated.content)
        assertNotEquals(question, updated)

        // DELETE by id deletes existing Question
        response = client.delete("${Routes.ALL_DECKS}/${deck.id}/${Routes.ALL_QUESTIONS}/${question.id}")

        assertEquals(HttpStatusCode.OK, response.status)

        response = client.delete("${Routes.ALL_DECKS}/${deck.id}/${Routes.ALL_QUESTIONS}/${question.id}")
        // otherwise gives 404 Not Found
        assertEquals(HttpStatusCode.NotFound, response.status)

        response = client.get("${Routes.ALL_DECKS}/${deck.id}/${Routes.ALL_QUESTIONS}")
        allQuestions = response.bodyAsText().toDataClass()

        assertEquals(1, allQuestions.size)

        // DELETE Deck deletes its questions
        client.delete("${Routes.ALL_DECKS}/${deck.id}")
        response = client.get("${Routes.ALL_DECKS}/${deck.id}/${Routes.ALL_QUESTIONS}/${allQuestions.first().id}")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}