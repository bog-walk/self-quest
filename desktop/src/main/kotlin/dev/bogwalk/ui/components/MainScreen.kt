package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import dev.bogwalk.ui.style.*
import dev.bogwalk.models.MainState
import dev.bogwalk.models.QuizMode

@Composable
fun MainScreen(
    screenState: MainState,
    mode: QuizMode,
    qOrder: Pair<Int, Int>?,
    onBackRequested: () -> Unit,
    onForwardRequested: () -> Unit,
    onAddRequested: () -> Unit,
    onEditRequested: () -> Unit,
    onDeleteRequested: () -> Unit,
    content: @Composable (BoxScope.(modifier: Modifier) -> Unit)
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (mode == QuizMode.STUDYING) {
            VerticalMenu(
                Modifier.align(Alignment.BottomEnd), screenState, onAddRequested, onEditRequested, onDeleteRequested
            )
        }
        if (screenState == MainState.IN_QUESTION && qOrder != null) {
            // cannot navigate to previous question if in a quiz
            if (mode == QuizMode.STUDYING || mode == QuizMode.CHECKED) {
                IconButton(
                    onClick = { onBackRequested() },
                    modifier = Modifier.testTag(BACK_TAG).padding(cardElevation).align(Alignment.TopStart),
                    enabled = qOrder.first != 0
                ) {
                    Icon(
                        painter = painterResource(BACK_ICON),
                        contentDescription = BACK_DESCRIPTION,
                        modifier = Modifier.requiredSize(iconSize)
                    )
                }
            }
            // if not studying, cannot move forward until answer has been chosen
            IconButton(
                onClick = { onForwardRequested() },
                modifier = Modifier.testTag(FORWARD_TAG).padding(cardElevation).align(Alignment.TopEnd),
                enabled = mode != QuizMode.WAITING && qOrder.first != qOrder.second
            ) {
                Icon(
                    painter = painterResource(FORWARD_ICON),
                    contentDescription = FORWARD_DESCRIPTION,
                    modifier = Modifier.requiredSize(iconSize)
                )
            }
        }
        content(Modifier.align(Alignment.TopCenter))
    }
}

@Composable
@Preview
private fun MainScreenWithDeckListPreview() {
    SelfQuestTheme {
        MainScreen(MainState.ALL_DECKS, QuizMode.STUDYING, null, {}, {}, {}, {}, {}) {
            DeckList(List(5) { Deck(it+1, "Title", 0) }) {}
        }
    }
}

@Composable
@Preview
private fun MainScreenWithDeckOverviewPreview() {
    SelfQuestTheme {
        MainScreen(MainState.DECK_OVERVIEW, QuizMode.STUDYING, null, {}, {}, {}, {}, {}) {
            QuestionList(List(10) { Question(it+1, "This is a question", "A", "B",
            "C", "D", "C") }) {}
        }
    }
}

@Composable
@Preview
private fun MainScreenWithFirstQuestionPreview() {
    SelfQuestTheme {
        MainScreen(MainState.IN_QUESTION, QuizMode.CHOSEN, 0 to 10, {}, {}, {}, {}, {}) {
            QuestionScreen(
                Question(1, "This is a question", "A", "B", "C", "D", "C"),
                1, 10, QuizMode.CHOSEN, "C"
            ) {}
        }
    }
}

@Composable
@Preview
private fun MainScreenWithMiddleQuestionPreview() {
    SelfQuestTheme {
        MainScreen(MainState.IN_QUESTION, QuizMode.CHOSEN, 5 to 10, {}, {}, {}, {}, {}) {
            QuestionScreen(
                Question(6, "This is a question", "A", "B", "C", "D", "C"),
                6, 10, QuizMode.CHOSEN, "A"
            ) {}
        }
    }
}

@Composable
@Preview
private fun MainScreenWithLastQuestionPreview() {
    SelfQuestTheme {
        MainScreen(MainState.IN_QUESTION, QuizMode.STUDYING, 10 to 10, {}, {}, {}, {}, {}) {
            QuestionScreen(
                Question(11, "This is a question", "A", "B", "C", "D", "C"),
                11, 11, QuizMode.STUDYING, ""
            ) {}
        }
    }
}