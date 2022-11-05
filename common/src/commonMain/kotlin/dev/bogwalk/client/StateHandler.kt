package dev.bogwalk.client

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.bogwalk.models.*

abstract class StateHandler : Client {
    var deckCache by mutableStateOf(emptyList<Deck>())
    var questionsCache by mutableStateOf(emptyList<Question>())

    var isLoadingCollections by mutableStateOf(false)
    var mainScreenState by mutableStateOf(MainState.ALL_DECKS)
    var currentDeck by mutableStateOf<Deck?>(null)
    var currentQuestion by mutableStateOf<Question?>(null)
    var quizMode by mutableStateOf(QuizMode.STUDYING)
    var questionOrder by mutableStateOf(0 to 0)
    var chosenAnswer by mutableStateOf("")
    var isDeleteDialogOpen by mutableStateOf(false)
    var isWarningDialogOpen by mutableStateOf(false)

    fun resetQuizMode() {
        when (quizMode) {
            QuizMode.CHECKED -> quizMode = QuizMode.STUDYING
            QuizMode.CHOSEN -> quizMode = QuizMode.WAITING
            else -> {}
        }
    }

    fun toggleQuizMode() {
        // navigating to deck overview defaults toggle to unchecked (studying)
        if (mainScreenState == MainState.DECK_OVERVIEW) {
            quizMode = QuizMode.WAITING
            showQuestion(1 to questionsCache.first())
        } else if (mainScreenState == MainState.IN_QUESTION || mainScreenState == MainState.IN_REVIEW) {
            chosenAnswer = ""
            quizMode = when (quizMode) {
                QuizMode.CHECKED, QuizMode.STUDYING -> {
                    mainScreenState = MainState.IN_QUESTION
                    QuizMode.WAITING
                }
                QuizMode.CHOSEN, QuizMode.WAITING -> QuizMode.STUDYING
            }
        }
    }

    fun toggleQuestionState() {
        mainScreenState = if (mainScreenState == MainState.IN_QUESTION) {
            MainState.IN_REVIEW
        } else {
            MainState.IN_QUESTION
        }
    }

    // only called from deck overview so no need to adjust quizMode
    fun showQuestion(question: Pair<Int, Question>) {
        currentQuestion = question.second
        mainScreenState = MainState.IN_QUESTION
        questionOrder = question.first to questionsCache.size
        chosenAnswer = ""
    }

    fun assessAnswer(chosen: String) {
        when (quizMode) {
            QuizMode.WAITING -> quizMode = QuizMode.CHOSEN
            QuizMode.STUDYING -> quizMode = QuizMode.CHECKED
            else -> {}
        }
        chosenAnswer = chosen
    }

    fun showAddNewScreen() {
        mainScreenState = when (mainScreenState) {
            MainState.ALL_DECKS -> MainState.UPDATING_DECK
            MainState.DECK_OVERVIEW -> MainState.UPDATING_QUESTION
            MainState.IN_QUESTION -> {
                currentQuestion = null
                MainState.UPDATING_QUESTION
            }
            // VerticalMenu will be disabled when updating so this is never reached
            else -> mainScreenState
        }
    }

    fun showEditScreen() {
        mainScreenState = when (mainScreenState) {
            MainState.DECK_OVERVIEW -> MainState.UPDATING_DECK
            MainState.IN_QUESTION -> MainState.UPDATING_QUESTION
            MainState.IN_REVIEW -> MainState.UPDATING_REVIEW
            // VerticalMenu will be disabled when updating so this is never reached
            else -> mainScreenState
        }
    }

    fun showDeleteDialog() {
        isDeleteDialogOpen = true
    }

    fun closeDeleteDialog() {
        isDeleteDialogOpen = false
    }

    fun showPreviousQuestion() {
        resetQuizMode()
        chosenAnswer = ""   // should history be maintained to show if answered?
        questionOrder = questionOrder.first - 1 to questionOrder.second
        currentQuestion = questionsCache[questionOrder.first - 1]
    }

    fun showNextQuestion() {
        resetQuizMode()
        chosenAnswer = ""
        questionOrder = questionOrder.first + 1 to questionOrder.second
        currentQuestion = questionsCache[questionOrder.first - 1]
    }

    fun handleBackButton() {
        when (mainScreenState) {
            MainState.DECK_OVERVIEW -> {
                mainScreenState = MainState.ALL_DECKS
                currentDeck = null
                quizMode = QuizMode.STUDYING
            }
            MainState.IN_QUESTION, MainState.IN_REVIEW -> {
                mainScreenState = MainState.DECK_OVERVIEW
                currentQuestion = null
                quizMode = QuizMode.STUDYING
                questionOrder = 0 to 0
            }
            MainState.UPDATING_DECK, MainState.UPDATING_QUESTION, MainState.UPDATING_REVIEW -> {
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
        closeWarningDialog()
    }
}