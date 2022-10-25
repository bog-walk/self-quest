package dev.bogwalk.client

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.bogwalk.models.Deck
import dev.bogwalk.models.MainState
import dev.bogwalk.models.Question
import dev.bogwalk.models.QuizMode
import kotlinx.coroutines.runBlocking

class SQAppState(private val api: SQClient = SQClient()) {
    var deckCache by mutableStateOf(emptyList<Deck>())
    // should this be an individual list with a db call every time a new deck is chosen?
    var questionsCache by mutableStateOf(mutableMapOf<Int, List<Question>>())

    var mainScreenState by mutableStateOf(MainState.ALL_DECKS)
    var currentDeck: Deck? by mutableStateOf(null)
    var currentQuestion: Question? by mutableStateOf(null)
    var quizMode by mutableStateOf(QuizMode.STUDYING)
    var questionOrder: Pair<Int, Int>? by mutableStateOf(null)
    var chosenAnswer by mutableStateOf("")
    var isDeleteDialogOpen by mutableStateOf(false)
    var isWarningDialogOpen by mutableStateOf(false)

    // is this the correct way to run api requests?
    fun loadSavedDecks() {
        deckCache = runBlocking { api.allDecks() }
    }

    // should this receive a Deck?
    fun showDeck(id: Int) {
        currentDeck = deckCache.first { it.id == id }
        currentDeck?.let {
            questionsCache[it.id] = runBlocking { api.allQuestions(it.id) }
        }
        mainScreenState = MainState.DECK_OVERVIEW
    }

    fun showQuestion(id: Int) {
        currentQuestion = questionsCache[currentDeck?.id]?.first { it.id == id }
        mainScreenState = MainState.IN_QUESTION
        if (quizMode == QuizMode.CHOSEN) {
            quizMode = QuizMode.WAITING
            chosenAnswer = ""
        }
    }

    fun assessAnswer(chosen: String) {
        quizMode = QuizMode.CHOSEN
        chosenAnswer = chosen
    }

    fun showAddNewScreen() {
        mainScreenState = when (mainScreenState) {
            MainState.ALL_DECKS -> MainState.UPDATING_DECK
            MainState.DECK_OVERVIEW -> MainState.UPDATING_QUESTION
            // VerticalMenu will be disabled when updating so this is never reached
            else -> mainScreenState
        }
    }

    fun addNewDeck(newDeck: Deck) {
        val added = runBlocking { api.addNewDeck(newDeck.name) }
        added?.let {
            deckCache = deckCache + added
            currentDeck = it
            mainScreenState = MainState.DECK_OVERVIEW
        }
    }

    fun addNewQuestion(newQuestion: Question) {
        val added = runBlocking {
            api.addNewQuestion(
                currentDeck!!.id, newQuestion.content, newQuestion.optionalAnswer1, newQuestion.optionalAnswer2,
                newQuestion.optionalAnswer3, newQuestion.optionalAnswer4, newQuestion.expectedAnswer
            )
        }
        added?.let { q ->
            currentDeck?.let { d ->
                questionsCache[d.id] = questionsCache.getValue(d.id)  + added
            }
            currentQuestion = q
            mainScreenState = MainState.IN_QUESTION
        }
    }

    fun showEditScreen() {
        mainScreenState = when (mainScreenState) {
            MainState.DECK_OVERVIEW -> MainState.UPDATING_DECK
            MainState.IN_QUESTION -> MainState.UPDATING_QUESTION
            // VerticalMenu will be disabled when updating so this is never reached
            else -> mainScreenState
        }
    }

    // client shouldn't be able to edit number of questions without adding a question
    fun editDeck(updated: Deck) {
        currentDeck?.let {
            runBlocking { api.editDeck(it.id, updated.name, it.size) }
            deckCache = deckCache - it + updated
            mainScreenState = MainState.DECK_OVERVIEW
        }
    }

    fun editQuestion(updated: Question) {
        currentQuestion?.let { q ->
            currentDeck?.let { d ->
                runBlocking { api.editQuestion(
                    d.id, q.id, updated.content, updated.optionalAnswer1, updated.optionalAnswer2,
                    updated.optionalAnswer3, updated.optionalAnswer4, updated.expectedAnswer
                ) }
                questionsCache[d.id] = questionsCache.getValue(d.id) - q + updated
            }
            mainScreenState = MainState.IN_QUESTION
        }
    }

    fun showDeleteDialog() {
        isDeleteDialogOpen = true
    }

    fun closeDeleteDialog() {
        isDeleteDialogOpen = false
    }

    fun confirmDelete() {
        when (mainScreenState) {
            MainState.DECK_OVERVIEW -> {
                currentDeck?.let {
                    runBlocking { api.deleteDeck(it.id) }
                    deckCache = deckCache - it
                }
                currentDeck = null
                mainScreenState = MainState.ALL_DECKS
            }

            MainState.IN_QUESTION -> {
                currentQuestion?.let { q ->
                    currentDeck?.let { d ->
                        runBlocking { api.deleteQuestion(q.id, d.id) }
                        questionsCache[d.id] = questionsCache.getValue(d.id) - q
                        deckCache = deckCache - d + d.copy(size = d.size - 1)
                    }
                }
            }
            // VerticalMenu will be disabled when updating so this is never reached
            else -> return
        }
    }

    // impossible to invoke if no previous questions so no risk of IOOB error
    fun showPreviousQuestion() {
        toggleQuizMode()
        chosenAnswer = ""   // should history be maintained to show if answered?
        currentDeck?.let {
            val index = questionsCache[it.id]?.indexOf(currentQuestion)
            if (index != null) {
                  currentQuestion = questionsCache[it.id]?.get(index - 1)
            }
        }
    }

    // impossible to invoke if no further questions so no risk of IOOB error
    fun showNextQuestion() {
        toggleQuizMode()
        chosenAnswer = ""
        currentDeck?.let {
            val index = questionsCache[it.id]?.indexOf(currentQuestion)
            if (index != null) {
                currentQuestion = questionsCache[it.id]?.get(index + 1)
            }
        }
    }

    private fun toggleQuizMode() {
        quizMode = when (quizMode) {
            QuizMode.CHECKED -> QuizMode.STUDYING
            QuizMode.CHOSEN -> QuizMode.WAITING
            else -> quizMode
        }
    }

    fun handleBackButton() {
        when (mainScreenState) {
            MainState.DECK_OVERVIEW -> {
                mainScreenState = MainState.ALL_DECKS
                currentDeck = null
                quizMode = QuizMode.STUDYING
            }
            MainState.IN_QUESTION -> {
                mainScreenState = MainState.DECK_OVERVIEW
                currentQuestion = null
                quizMode = QuizMode.STUDYING
            }
            MainState.UPDATING_DECK, MainState.UPDATING_QUESTION -> {
                isWarningDialogOpen = true
            }
            else -> {}  // back button not displayed on all decks screen
        }
    }

    fun closeWarningDialog() {
        isWarningDialogOpen = false
    }

    fun confirmLeaveForm() {
        mainScreenState = if (currentDeck == null) {
            MainState.ALL_DECKS
        } else if (currentQuestion == null) {
            MainState.DECK_OVERVIEW
        } else MainState.IN_QUESTION
    }
}