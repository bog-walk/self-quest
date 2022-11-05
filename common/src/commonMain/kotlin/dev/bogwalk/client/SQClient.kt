package dev.bogwalk.client

import dev.bogwalk.models.*
import dev.bogwalk.routes.Decks
import dev.bogwalk.routes.ServerShutdown
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.resources.get
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*

class SQClient(
    private val coroutineScope: CoroutineScope
) : StateHandler() {
    private val client = HttpClient(Java) {
        install(Resources)
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            host = "0.0.0.0"
            port = 8080
            url { protocol = URLProtocol.HTTP }
        }
        expectSuccess = true
    }

    override fun cleanUp() {
        coroutineScope.launch {
            client.get(ServerShutdown())
            client.close()
        }
    }

    override suspend fun loadSavedDecks() {
        deckCache = client.get(Decks()).body()
    }

    override fun showDeck(deck: Deck) {
        coroutineScope.launch {
            try {
                questionsCache = client.get(
                    Decks.DeckId.Questions(Decks.DeckId(id_d = deck.id))
                ).body()
                currentDeck = deck
                mainScreenState = MainState.DECK_OVERVIEW
            } catch (ex: ClientRequestException) {
                // refresh cache until better exception handling implemented
                loadSavedDecks()
            }
        }
    }

    override fun addNewDeck(name: String) {
        coroutineScope.launch {
            val newId = client.post(Decks()) {
                contentType(ContentType.Application.Json)
                setBody(name)
            }.body<Int>()
            currentDeck = Deck(newId, name, 0)
            deckCache = listOf(currentDeck!!) + deckCache
            questionsCache = emptyList()
            mainScreenState = MainState.DECK_OVERVIEW
        }
    }

    override fun editDeck(updated: Deck) {
        coroutineScope.launch {
            try {
                currentDeck?.let {
                    client.put(Decks.DeckId(id_d = updated.id)) {
                        contentType(ContentType.Application.Json)
                        setBody(updated)
                    }
                    deckCache = listOf(updated) + (deckCache - it)
                    currentDeck = updated
                    mainScreenState = MainState.DECK_OVERVIEW
                }
            } catch (ex: ClientRequestException) {
                // refresh cache until better exception handling implemented
                loadSavedDecks()
                currentDeck = null
                questionsCache = emptyList()
                mainScreenState = MainState.ALL_DECKS
            }
        }
    }

    override fun addNewQuestion(newQuestion: Question) {
        coroutineScope.launch {
            currentDeck?.let {
                val newId = client.post(
                    Decks.DeckId.Questions(Decks.DeckId(id_d = it.id))
                ) {
                    contentType(ContentType.Application.Json)
                    setBody(newQuestion)
                }.body<Int>()
                currentQuestion = newQuestion.copy(id = newId)
                questionsCache = listOf(currentQuestion!!) + questionsCache
                questionOrder = 1 to questionsCache.size
                currentDeck = it.copy(size = it.size + 1)
                deckCache = listOf(currentDeck!!) + deckCache - it
                mainScreenState = MainState.IN_QUESTION
            }
        }
    }

    override fun editQuestion(updated: Question) {
        resetQuizMode()
        coroutineScope.launch {
            try {
                currentQuestion?.let {
                    client.put(
                        Decks.DeckId.Questions.QuestionId(
                            Decks.DeckId.Questions(Decks.DeckId(id_d = currentDeck!!.id)), id_q = it.id
                        )
                    ) {
                        contentType(ContentType.Application.Json)
                        setBody(updated)
                    }
                    val currentIndex = questionOrder.first - 1
                    questionsCache = questionsCache.slice(0 until currentIndex) + listOf(updated) +
                            questionsCache.slice(currentIndex + 1..questionsCache.lastIndex)
                    currentQuestion = updated
                    mainScreenState = MainState.IN_QUESTION
                }
            } catch (ex: ClientRequestException) {
                // refresh cache until better exception handling implemented
                loadSavedDecks()
                currentDeck?.let {
                    showDeck(it)
                }
            }
        }
    }

    override fun updateReview(updated: Review) {
        resetQuizMode()
        coroutineScope.launch {
            try {
                currentQuestion?.let {
                    client.put(
                        // surely there's away to avoid this and still maintain path nesting?
                        // extract to a links file?
                        Decks.DeckId.Questions.QuestionId.QReview(
                            Decks.DeckId.Questions.QuestionId(
                                Decks.DeckId.Questions(Decks.DeckId(id_d = currentDeck!!.id)), id_q = it.id
                            )
                        )
                    ) {
                        contentType(ContentType.Application.Json)
                        setBody(updated)
                    }
                    val currentIndex = questionOrder.first - 1
                    currentQuestion = it.copy(review = updated)
                    questionsCache = questionsCache.slice(0 until currentIndex) +
                            listOf(currentQuestion!!) +
                            questionsCache.slice(currentIndex + 1..questionsCache.lastIndex)
                    mainScreenState = MainState.IN_QUESTION
                }
            } catch (ex: ClientRequestException) {
                // refresh cache until better exception handling implemented
                loadSavedDecks()
                currentDeck?.let {
                    showDeck(it)
                }
            }
        }
    }

    override fun confirmDelete() {
        coroutineScope.launch {
            when (mainScreenState) {
                MainState.DECK_OVERVIEW -> {
                    currentDeck?.let {
                        try {
                            client.delete(Decks.DeckId(id_d = it.id))
                            deckCache -= it
                            currentDeck = null
                            mainScreenState = MainState.ALL_DECKS
                        } catch (ex: ClientRequestException) {
                            // refresh cache until better exception handling implemented
                            loadSavedDecks()
                        }
                    }
                }
                MainState.IN_QUESTION -> {
                    currentQuestion?.let {
                        try {
                            client.delete(
                                Decks.DeckId.Questions.QuestionId(
                                    Decks.DeckId.Questions(Decks.DeckId(id_d = currentDeck!!.id)), id_q = it.id
                                )
                            )
                            questionsCache -= it
                            currentDeck?.let { d ->
                                deckCache = listOf(d.copy(size = d.size - 1)) + deckCache - d
                            }
                            currentQuestion = null
                            mainScreenState = MainState.DECK_OVERVIEW
                        } catch (ex: ClientRequestException) {
                            // refresh cache until better exception handling implemented
                            loadSavedDecks()
                            currentDeck?.let { deck ->
                                showDeck(deck)
                            }
                        }
                    }
                }
                MainState.IN_REVIEW -> {
                    currentQuestion?.let { q ->
                        q.review?.let {
                            try {
                                client.delete(
                                    Decks.DeckId.Questions.QuestionId.QReview(
                                        Decks.DeckId.Questions.QuestionId(
                                            Decks.DeckId.Questions(Decks.DeckId(id_d = currentDeck!!.id)),
                                            id_q = q.id
                                        )
                                    )
                                )
                                val currentIndex = questionOrder.first - 1
                                currentQuestion = currentQuestion?.copy(review = null)
                                questionsCache = questionsCache.slice(0 until currentIndex) +
                                        listOf(currentQuestion!!) +
                                        questionsCache.slice(currentIndex + 1..questionsCache.lastIndex)
                                mainScreenState = MainState.IN_QUESTION
                            } catch (ex: ClientRequestException) {
                                // refresh cache until better exception handling implemented
                                loadSavedDecks()
                                currentDeck?.let { deck ->
                                    showDeck(deck)
                                }
                            }
                        }
                    }
                }
                // VerticalMenu will be disabled when updating so this is never reached
                else -> {}
            }
        }
        closeDeleteDialog()
    }
}