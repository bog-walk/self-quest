package dev.bogwalk.routes

import dev.bogwalk.models.q4
import dev.bogwalk.models.questionStorage
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

internal class QuestionRoutesTest {
    @Test
    fun `GET returns all stored Question instances`() = testApplication {
        val response = client.get("/question")
        val json = Json

        assertEquals(json.encodeToString(questionStorage), response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET question by id returns existing Question`() = testApplication {
        val response = client.get("/question/q1")
        val json = Json
        val expected = json.encodeToString(questionStorage.first())

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expected, response.bodyAsText())
    }

    @Test
    fun `GET question by id flags invalid Question id`() = testApplication {
        val response = client.get("/question/q100")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `POST adds a new Question`() = testApplication {
        assertEquals(3, questionStorage.size)
        val response = client.post("/question") {
            header(
                HttpHeaders.ContentType,
                ContentType.Application.FormUrlEncoded
            )
            setBody(listOf(
                "id" to q4.id, "content" to q4.content,
                "answers" to q4.optionalAnswers.joinToString(" "), "expected" to q4.expectedAnswer
            ).formUrlEncode())
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(4, questionStorage.size)
    }

    @Test
    fun `GET edit question by id edits existing Question`() = testApplication {
        val newContent = "Which species is NOT affected by foot & mouth disease?"
        val response = client.get("/question/q1/edit") {
            header(
                HttpHeaders.ContentType,
                ContentType.Application.FormUrlEncoded
            )
            setBody(listOf("content" to newContent).formUrlEncode())
        }

        assertEquals(HttpStatusCode.Accepted, response.status)
        assertEquals("q1", questionStorage.first().id)
        assertEquals(newContent, questionStorage.first().content)
    }

    @Test
    fun `GET edit question by id flags invalid Question id`() = testApplication {
        val response = client.get("/question/q100/edit")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }
    // JUnit5 TestMethodOrder does not force requests in order so POST test occurs after DELETE and fails
    // Refactor tests after database persistence implementation
    /*
    @Test
    fun `DELETE by id removes a Question`() = testApplication {
        assertEquals(3, questionStorage.size)
        val response1 = client.delete("/question/q2")

        assertEquals(HttpStatusCode.Accepted, response1.status)
        assertEquals(2, questionStorage.size)

        val response2 = client.delete("/question/q2")

        assertEquals(HttpStatusCode.NotFound, response2.status)
    }*/
}