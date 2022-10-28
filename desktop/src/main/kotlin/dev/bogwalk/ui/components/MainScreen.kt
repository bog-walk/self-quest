package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import dev.bogwalk.ui.style.*
import dev.bogwalk.models.MainState
import dev.bogwalk.models.QuizMode

@Composable
fun MainScreen(
    screenState: MainState,
    mode: QuizMode,
    qOrder: Pair<Int, Int>,
    onBackRequested: () -> Unit,
    onForwardRequested: () -> Unit,
    onAddRequested: () -> Unit,
    onEditRequested: () -> Unit,
    onDeleteRequested: () -> Unit,
    content: @Composable (BoxScope.() -> Unit)
) {
    Box(
        modifier = Modifier.padding(top = cardPadding).fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (mode == QuizMode.STUDYING || mode == QuizMode.CHECKED) {
            VerticalMenu(
                Modifier.align(Alignment.BottomEnd), screenState, onAddRequested, onEditRequested, onDeleteRequested
            )
        }
        if (screenState == MainState.IN_QUESTION || screenState == MainState.IN_REVIEW) {
            // cannot navigate to previous question if in a quiz
            if (mode == QuizMode.STUDYING || mode == QuizMode.CHECKED) {
                ArrowButton(
                    modifier = Modifier.align(Alignment.TopStart),
                    isEnabled = qOrder.first > 1,
                    onButtonClick = onBackRequested
                )
            }
            // if not studying, cannot move forward until answer has been chosen
            ArrowButton(
                modifier = Modifier.align(Alignment.TopEnd),
                isEnabled = mode != QuizMode.WAITING && qOrder.first != qOrder.second,
                isBackArrow = false,
                onButtonClick = onForwardRequested
            )
        }
        content()
    }
}

@Composable
@Preview
private fun MainScreenWithDeckListPreview() {
    SelfQuestTheme {
        MainScreen(MainState.ALL_DECKS, QuizMode.STUDYING, 0 to 0, {}, {}, {}, {}, {}) {
            DeckList(List(5) { Deck(it+1, "Title", 0) }) {}
        }
    }
}

@Composable
@Preview
private fun MainScreenWithDeckOverviewPreview() {
    SelfQuestTheme {
        MainScreen(MainState.DECK_OVERVIEW, QuizMode.STUDYING, 0 to 0, {}, {}, {}, {}, {}) {
            QuestionList(List(10) { Question(it+1, "This is a question", "A", "B",
            "C", "D", "C", null) }) {}
        }
    }
}

@Composable
@Preview
private fun MainScreenWithFirstQuestionPreview() {
    SelfQuestTheme {
        MainScreen(MainState.IN_QUESTION, QuizMode.CHOSEN, 1 to 10, {}, {}, {}, {}, {}) {
            QuestionScreen(
                Question(1, "This is a question", "A", "B", "C", "D", "C", null),
                1, 10, MainState.IN_QUESTION, QuizMode.CHOSEN, "C"
            , {}) {}
        }
    }
}

@Composable
@Preview
private fun MainScreenWithMiddleQuestionPreview() {
    SelfQuestTheme {
        MainScreen(MainState.IN_QUESTION, QuizMode.CHOSEN, 5 to 10, {}, {}, {}, {}, {}) {
            QuestionScreen(
                Question(6, "This is a question", "A", "B", "C", "D", "C", null),
                6, 10, MainState.IN_QUESTION, QuizMode.CHOSEN, "A"
            , {}) {}
        }
    }
}

@Composable
@Preview
private fun MainScreenWithLastQuestionPreview() {
    SelfQuestTheme {
        MainScreen(MainState.IN_QUESTION, QuizMode.STUDYING, 10 to 10, {}, {}, {}, {}, {}) {
            QuestionScreen(
                Question(11, "This is a question", "A", "B", "C", "D", "C", null),
                11, 11, MainState.IN_QUESTION, QuizMode.STUDYING, ""
            , {}) {}
        }
    }
}