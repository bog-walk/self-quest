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
            state.mainScreenState.value,
            state.currentDeck.value?.name,
            state::handleBackButton
        ) {
            if (state.mainScreenState.value in MainState.values().slice(1..3)) {
                QuizModeSwitch(state.quizMode.value) { state.toggleQuizMode() }
            }
        }
        MainScreen(
            state.mainScreenState.value,
            state.quizMode.value,
            state.questionOrder.value,
            state::showPreviousQuestion,
            state::showNextQuestion,
            state::showAddNewScreen,
            state::showEditScreen,
            state::showDeleteDialog
        ) {
            when (state.mainScreenState.value) {
                MainState.ALL_DECKS -> DeckList(state.deckCache.value) { state.showDeck(it) }
                MainState.DECK_OVERVIEW -> QuestionList(state.questionsCache.value) { state.showQuestion(it) }
                MainState.IN_QUESTION, MainState.IN_REVIEW -> QuestionScreen(
                    state.currentQuestion.value!!, state.questionOrder.value.first, state.questionOrder.value.second,
                    state.mainScreenState.value, state.quizMode.value, state.chosenAnswer.value, state::toggleQuestionState
                ) { state.assessAnswer(it) }
                MainState.UPDATING_DECK -> DeckDataForm(state.currentDeck.value) {
                    if (state.currentDeck.value == null) state.addNewDeck(it.name) else state.editDeck(it)
                }
                MainState.UPDATING_QUESTION -> QuestionDataForm(state.currentQuestion.value) {
                    if (state.currentQuestion.value == null) state.addNewQuestion(it) else state.editQuestion(it)
                }
                MainState.UPDATING_REVIEW -> ReviewDataForm(state.currentQuestion.value?.review) {
                    state.updateReview(it)
                }
            }
        }
    }
}