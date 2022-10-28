package dev.bogwalk.client

import dev.bogwalk.databases.DAOFacade
import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import dev.bogwalk.routes.Decks
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.resources.get
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

class SQClient(engine: HttpClientEngine? = null) : DAOFacade {
    private val client = httpClient(engine) {
        install(Resources)
        install(ContentNegotiation) {
            json()
        }

        // throws exception for non-2xx responses
        expectSuccess = true
    }

    fun cleanUp() = client.close()

    override suspend fun allDecks(): List<Deck> = client.get(Decks()).body()

    override suspend fun deck(id: Int): Deck? {
        return try {
            client.get(Decks.DeckId(id_d = id)).body<Deck>()
        } catch (ex: ClientRequestException) {
            null
        }
    }

    override suspend fun addNewDeck(name: String): Deck? {
        return try {
            client.post(Decks()) {
                contentType(ContentType.Application.Json)
                setBody(Deck(1, name, 0))
            }.body<Deck>()
        } catch (ex: ServerResponseException) {
            null
        }
    }

    override suspend fun editDeck(id: Int, name: String, size: Int): Boolean {
        return try {
            client.put(Decks.DeckId(id_d = id)) {
                contentType(ContentType.Application.Json)
                setBody(Deck(1, name, size))
            }
            true
        } catch (ex: ClientRequestException) {
            false
        }
    }

    override suspend fun deleteDeck(id: Int): Boolean {
        return try {
            client.delete(Decks.DeckId(id_d = id))
            true
        } catch (ex: ClientRequestException) {
            false
        }
    }

    override suspend fun allQuestions(deckId: Int): List<Question> {
        return try {
            client.get(Decks.DeckId.Questions(Decks.DeckId(id_d = deckId))).body()
        } catch (ex: ResponseException) {
            emptyList()
        }
    }

    // deckId isn't strictly necessary for dao
    override suspend fun question(id: Int, deckId: Int): Question? {
        return try {
            client.get(Decks.DeckId.Questions.QuestionId(Decks.DeckId.Questions(Decks.DeckId(id_d=deckId)), id_q=id)).body<Question>()
        } catch (ex: ClientRequestException) {
            null
        }
    }

    override suspend fun addNewQuestion(
        deckId: Int,
        content: String,
        option1: String,
        option2: String,
        option3: String,
        option4: String,
        correct: String
    ): Question? {
        return try {
            client.post(Decks.DeckId.Questions(Decks.DeckId(id_d=deckId))) {
                contentType(ContentType.Application.Json)
                setBody(Question(1, content, option1, option2, option3, option4, correct, null))
            }.body<Question>()
        } catch (ex: ServerResponseException) {
            null
        }
    }

    override suspend fun editQuestion(
        deckId: Int,
        id: Int,
        content: String,
        option1: String,
        option2: String,
        option3: String,
        option4: String,
        correct: String
    ): Boolean {
        return try {
            client.put(Decks.DeckId.Questions.QuestionId(Decks.DeckId.Questions(Decks.DeckId(id_d=deckId)), id_q=id)) {
                contentType(ContentType.Application.Json)
                setBody(Question(1, content, option1, option2, option3, option4, correct, null))
            }
            true
        } catch (ex: ClientRequestException) {
            false
        }
    }

    override suspend fun deleteQuestion(id: Int, deckId: Int): Boolean {
        return try {
            client.delete(Decks.DeckId.Questions.QuestionId(Decks.DeckId.Questions(Decks.DeckId(id_d=deckId)), id_q=id))
            true
        } catch (ex: ClientRequestException) {
            false
        }
    }
}