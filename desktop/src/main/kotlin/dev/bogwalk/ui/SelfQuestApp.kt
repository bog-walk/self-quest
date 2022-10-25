package dev.bogwalk.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import dev.bogwalk.ui.components.*
import dev.bogwalk.models.MainState
import dev.bogwalk.client.SQAppState

@Composable
fun SelfQuestApp(state: SQAppState) {
    Row {
        LeftSideBar(
            state.currentDeck?.name,
            state::handleBackButton
        ) {}  // metrics, etc?
        MainScreen(
            state.mainScreenState,
            state.quizMode,
            state.questionOrder,
            state::showPreviousQuestion,
            state::showNextQuestion,
            state::showAddNewScreen,
            state::showEditScreen,
            state::showDeleteDialog
        ) {
            when (state.mainScreenState) {
                MainState.ALL_DECKS -> DeckList(state.deckCache) { state.showDeck(it) }
                MainState.DECK_OVERVIEW -> QuestionList(
                    state.questionsCache[state.currentDeck?.id]!!
                ) { state.showQuestion(it) }
                MainState.IN_QUESTION -> QuestionScreen(
                    state.currentQuestion!!, state.questionOrder?.first!!, state.questionOrder?.second!!,
                    state.quizMode, state.chosenAnswer
                ) { state.assessAnswer(it) }
                MainState.UPDATING_DECK -> DeckDataForm(state.currentDeck) {
                    if (state.currentDeck == null) state.addNewDeck(it) else state.editDeck(it)
                }
                MainState.UPDATING_QUESTION -> QuestionDataForm(state.currentQuestion) {
                    if (state.currentQuestion == null) state.addNewQuestion(it) else state.editQuestion(it)
                }
            }
        }
    }
}