package dev.bogwalk.ui.integration

import dev.bogwalk.client.StateHandler
import dev.bogwalk.models.Deck
import dev.bogwalk.models.MainState
import dev.bogwalk.models.Question
import dev.bogwalk.models.Review

class TestClient(
    private val existingDecks: List<Deck> = emptyList(),
    private val existingQuestions: Map<Int, List<Question>> = emptyMap()
) : StateHandler() {
    var nextDeckId = existingDecks.size + 1
    private fun getNextQId(): Int {
        return (questionsCache.maxOfOrNull { it.id } ?: 0) + 1
    }

    override fun cleanUp() {
        println("Cleaning up fake databases...")
    }

    override suspend fun loadSavedDecks() {
        deckCache = existingDecks
    }

    // all changes to deck question will be lost every time a deck is exited, unless a local storage mapping is created
    override fun showDeck(deck: Deck) {
        currentDeck = deck
        questionsCache = existingQuestions.getOrDefault(deck.id, emptyList())
        mainScreenState = MainState.DECK_OVERVIEW
    }

    override fun addNewDeck(name: String) {
        val newWithId = Deck(nextDeckId++, name, 0)
        deckCache = listOf(newWithId) + deckCache
        currentDeck = newWithId
        questionsCache = emptyList()
        mainScreenState = MainState.DECK_OVERVIEW
    }

    override fun editDeck(updated: Deck) {
        currentDeck?.let {
            deckCache = listOf(updated) + deckCache - it
            currentDeck = updated
            mainScreenState = MainState.DECK_OVERVIEW
        }
    }

    // can only be called from deck overview and adds question to front of collection
    override fun addNewQuestion(newQuestion: Question) {
        val newWithId = newQuestion.copy(id = getNextQId())
        questionsCache = listOf(newWithId) + questionsCache
        currentQuestion = newWithId
        questionOrder = 1 to questionsCache.size
        currentDeck?.let {
            currentDeck = it.copy(size = it.size + 1)
            deckCache = listOf(currentDeck!!) + deckCache - it
        }
        mainScreenState = MainState.IN_QUESTION
    }

    // can only be called from in question and retains current question order
    override fun editQuestion(updated: Question) {
        resetQuizMode()
        currentQuestion?.let {
            val currentIndex = questionOrder.first - 1
            questionsCache = questionsCache.slice(0 until currentIndex) + listOf(updated) +
                    questionsCache.slice(currentIndex + 1..questionsCache.lastIndex)
            currentQuestion = updated
            mainScreenState = MainState.IN_QUESTION
        }
    }

    // form will not allow a null Review to be sent
    override fun updateReview(updated: Review) {
        currentQuestion?.let {
            val updatedQ = it.copy(review = updated)
            editQuestion(updatedQ)
        }
    }

    override fun confirmDelete() {
        when (mainScreenState) {
            MainState.DECK_OVERVIEW -> {
                currentDeck?.let {
                    deckCache -= it
                }
                currentDeck = null
                mainScreenState = MainState.ALL_DECKS
            }
            MainState.IN_QUESTION -> {
                currentQuestion?.let {
                    questionsCache -= it
                    currentDeck?.let { d ->
                        deckCache = listOf(d.copy(size = d.size - 1)) + deckCache - d
                    }
                }
                currentQuestion = null
                mainScreenState = MainState.DECK_OVERVIEW
            }
            MainState.IN_REVIEW -> {
                currentQuestion?.let { q ->
                    q.review?.let {
                        val updatedQ = q.copy(review = it)
                        editQuestion(updatedQ)
                    }
                }
            }
            // VerticalMenu will be disabled when updating so this is never reached
            else -> return
        }
        closeDeleteDialog()
    }
}