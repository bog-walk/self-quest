package dev.bogwalk.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.bogwalk.models.*
import dev.bogwalk.ui.style.*

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun DeckCard(
    deck: Deck,
    onDeckChosen: (Deck) -> Unit
) {
    var isInFocus by remember { mutableStateOf(false) }
    val titleColor: Color by animateColorAsState(
        targetValue = if (isInFocus) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
        animationSpec = tween(animationDuration)
    )

    Card(
        onClick = { onDeckChosen(deck) },
        modifier = Modifier.padding(cardPadding).fillMaxWidth()
            .onPointerEvent(PointerEventType.Enter) { isInFocus = true }
            .onPointerEvent(PointerEventType.Exit) { isInFocus = false },
        shape = MaterialTheme.shapes.large,
        backgroundColor = MaterialTheme.colors.background,
        elevation = cardElevation,
        onClickLabel = "View collection ${deck.name}",
        role = Role.Button
    ) {
        Row(
            modifier = Modifier.drawLeftBorder(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = deck.name,
                modifier = Modifier.weight(1f).padding(start = innerPadding),
                color = titleColor,
                style = MaterialTheme.typography.h5
            )
            Spacer(Modifier.width(innerPadding))
            Text(
                text = "${deck.size} ${if (deck.size != 1) "questions" else "question"}",
                modifier = Modifier.padding(innerPadding),
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
internal fun QuestionCard(question: String) {
    Card(
        modifier = Modifier.padding(cardPadding).fillMaxWidth()
            .heightIn(preferredHeight.first, preferredHeight.second),
        shape = MaterialTheme.shapes.large,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground,
        elevation = cardElevation
    ) {
        Text(
            text = question,
            modifier = Modifier.padding(innerPadding).wrapContentHeight(Alignment.CenterVertically),
            style = MaterialTheme.typography.body1
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun QuestionSummaryCard(
    index: Int,
    question: Question,
    onQuestionChosen: (Pair<Int, Question>) -> Unit
) {
    var isInFocus by remember { mutableStateOf(false) }
    val titleColor: Color by animateColorAsState(
        targetValue = if (isInFocus) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
        animationSpec = tween(animationDuration)
    )

    Card(
        onClick = { onQuestionChosen(index to question) },
        modifier = Modifier.padding(cardPadding).fillMaxWidth()
            .onPointerEvent(PointerEventType.Enter) { isInFocus = true }
            .onPointerEvent(PointerEventType.Exit) { isInFocus = false },
        shape = MaterialTheme.shapes.large,
        backgroundColor = MaterialTheme.colors.background,
        elevation = cardElevation,
        onClickLabel = "View question $index",
        role = Role.Button
    ) {
        Row(
            modifier = Modifier.drawLeftBorder(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Q$index",
                modifier = Modifier.padding(start = innerPadding),
                color = titleColor,
                style = MaterialTheme.typography.h5
            )
            Text(
                text = question.content,
                modifier = Modifier.padding(innerPadding),
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun AnswerCard(
    answer: String,
    quizMode: QuizMode,
    isCorrectAnswer: Boolean,
    isChosen: Boolean,
    onAnswerChosen: (String) -> Unit
) {
    Card(
        onClick = { onAnswerChosen(answer) },
        modifier = Modifier.testTag(ANSWER_TAG).padding(cardPadding).fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        backgroundColor = when (quizMode) {
            QuizMode.CHECKED, QuizMode.CHOSEN -> if (isCorrectAnswer) {
                MaterialTheme.colors.primary
            } else if (isChosen) {
                MaterialTheme.colors.error
            } else MaterialTheme.colors.secondary
            else -> MaterialTheme.colors.secondary
        },
        contentColor = MaterialTheme.colors.onSecondary,
        elevation = cardElevation,
        enabled = quizMode == QuizMode.STUDYING || quizMode == QuizMode.WAITING,
        onClickLabel = "Choose answer: $answer",
        role = Role.Button
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = answer,
                // weight forces long texts to not clip Icon
                modifier = Modifier.weight(1f).padding(innerPadding),
                style = MaterialTheme.typography.body1
            )
            if (isChosen) {
                Icon(
                    painter = painterResource(if (isCorrectAnswer) CORRECT_ICON else WRONG_ICON),
                    contentDescription = if (isCorrectAnswer) CORRECT_DESCRIPTION else WRONG_DESCRIPTION,
                    modifier = Modifier
                        .padding(0.dp, cardPadding, cardPadding, cardPadding)
                        .requiredSize(iconSize),
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

private fun Modifier.drawLeftBorder() = drawBehind {
    drawLine(
        color = SelfQuestColors.primary,
        start = Offset(0f, 0f),
        end = Offset(0f, size.height),
        strokeWidth = CARD_STROKE
    )
}

@Preview
@Composable
private fun DeckCardsPreview() {
    SelfQuestTheme {
        Column {
            DeckCard(Deck(1, "Equine", 56)) {}
            DeckCard(Deck(2, "Random Stuff", 1)) {}
            DeckCard(Deck(3, "Collection Name", 9999)) {}
        }
    }
}

@Preview
@Composable
private fun QuestionAndAnswersCardInStudyModePreview() {
    SelfQuestTheme {
        Column {
            QuestionCard(q2.content)
            AnswerCard(q2.optionalAnswer1, QuizMode.STUDYING, false, isChosen=false) {}
            AnswerCard(q2.optionalAnswer2, QuizMode.STUDYING, false, isChosen=false) {}
            AnswerCard(q2.optionalAnswer3, QuizMode.STUDYING, true, isChosen=false) {}
            AnswerCard(q2.optionalAnswer4, QuizMode.STUDYING, false, isChosen=false) {}
        }
    }
}

@Preview
@Composable
private fun QuestionAndAnswersCardInQuizModePreview() {
    SelfQuestTheme {
        Column {
            QuestionCard(q2.content)
            AnswerCard(q2.optionalAnswer1, QuizMode.WAITING, false, isChosen=false) {}
            AnswerCard(q2.optionalAnswer2, QuizMode.WAITING, false, isChosen=false) {}
            AnswerCard(q2.optionalAnswer3, QuizMode.WAITING, true, isChosen=false) {}
            AnswerCard(q2.optionalAnswer4, QuizMode.WAITING, false, isChosen=false) {}
        }
    }
}

@Preview
@Composable
private fun QuestionSummaryCardPreview() {
    SelfQuestTheme {
        Column {
            QuestionSummaryCard(1, Question(1, "This a short question.",
                "A", "B", "C", "D", "C")) {}
            QuestionSummaryCard(
                2, Question(2, "This is an example of a very very very long multiline very long string question, which is very long.",
                "A", "B", "C", "D", "C")
            ) {}
        }
    }
}

@Preview
@Composable
private fun AnsweredCorrectInQuizModePreview() {
    SelfQuestTheme {
        Column {
            AnswerCard(q4.optionalAnswer1, QuizMode.CHOSEN, false, isChosen=false) {}
            AnswerCard(q4.optionalAnswer2, QuizMode.CHOSEN, false, isChosen=false) {}
            AnswerCard(q4.optionalAnswer3, QuizMode.CHOSEN, isCorrectAnswer=true, isChosen=true) {}
            AnswerCard(q4.optionalAnswer4, QuizMode.CHOSEN, false, isChosen=false) {}
        }
    }
}

@Preview
@Composable
private fun AnsweredCorrectInStudyModePreview() {
    SelfQuestTheme {
        Column {
            AnswerCard(q4.optionalAnswer1, QuizMode.CHECKED, false, isChosen=false) {}
            AnswerCard(q4.optionalAnswer2, QuizMode.CHECKED, false, isChosen=false) {}
            AnswerCard(q4.optionalAnswer3, QuizMode.CHECKED, isCorrectAnswer=true, isChosen=true) {}
            AnswerCard(q4.optionalAnswer4, QuizMode.CHECKED, false, isChosen=false) {}
        }
    }
}

@Preview
@Composable
private fun AnsweredWrongPreview() {
    SelfQuestTheme {
        Column {
            AnswerCard(q4.optionalAnswer1, QuizMode.CHOSEN, isCorrectAnswer=false, isChosen=true) {}
            AnswerCard(q4.optionalAnswer2, QuizMode.CHOSEN, false, isChosen=false) {}
            AnswerCard(q4.optionalAnswer3, QuizMode.CHOSEN, isCorrectAnswer=true, isChosen=false) {}
            AnswerCard(q4.optionalAnswer4, QuizMode.CHOSEN, false, isChosen=false) {}
        }
    }
}

@Preview
@Composable
private fun VeryLongAnswerPreview() {
    val ans = "This is an example of a very very very long multiline very long string answer, which is very long"
    SelfQuestTheme {
        Column {
            AnswerCard(ans, QuizMode.WAITING, false, isChosen=false) {}
            AnswerCard(ans, QuizMode.CHOSEN, isCorrectAnswer=true, isChosen=true) {}
        }
    }
}