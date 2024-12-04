package dev.bogwalk.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import dev.bogwalk.ui.components.*
import dev.bogwalk.models.MainState
import dev.bogwalk.client.StateHandler

@Composable
fun SelfQuestApp(state: StateHandler) {
    Row {
        LeftSideBar(
            state.mainScreenState,
            state.currentDeck?.name,
            state::handleBackButton
        ) {
            if (state.mainScreenState in MainState.entries.toTypedArray().slice(1..3) &&
                state.questionsCache.isNotEmpty()) {
                QuizModeSwitch(state.quizMode) { state.toggleQuizMode() }
            }
        }
        MainScreen(
            state.isLoadingCollections,
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
                MainState.DECK_OVERVIEW -> QuestionList(state.questionsCache) { state.showQuestion(it) }
                MainState.IN_QUESTION, MainState.IN_REVIEW -> QuestionScreen(
                    state.currentQuestion!!, state.questionOrder.first, state.questionOrder.second,
                    state.mainScreenState, state.quizMode, state.chosenAnswer, state::toggleQuestionState
                ) { state.assessAnswer(it) }
                MainState.UPDATING_DECK -> DeckDataForm(state.currentDeck) {
                    if (state.currentDeck == null) state.addNewDeck(it.name) else state.editDeck(it)
                }
                MainState.UPDATING_QUESTION -> QuestionDataForm(state.currentQuestion) {
                    if (state.currentQuestion == null) state.addNewQuestion(it) else state.editQuestion(it)
                }
                MainState.UPDATING_REVIEW -> ReviewDataForm(state.currentQuestion?.review) {
                    state.updateReview(it)
                }
            }
        }
    }
}