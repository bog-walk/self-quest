package dev.bogwalk.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bogwalk.common.generated.resources.Res
import dev.bogwalk.common.generated.resources.correct
import dev.bogwalk.common.generated.resources.wrong
import dev.bogwalk.models.*
import dev.bogwalk.ui.style.*
import org.jetbrains.compose.resources.painterResource

const val DECK_TAG = "deck card"
const val QUEST_TAG = "question card"
const val CORRECT_DESCRIPTION = "Correct answer"
const val WRONG_DESCRIPTION = "Wrong answer"
const val ANSWER_TAG = "answer card"
private const val CARD_STROKE = 20f
private val preferredHeight = 150.dp to Dp.Unspecified

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun DeckCard(
    deck: Deck,
    onDeckChosen: (Deck) -> Unit
) {
    var isInFocus by remember { mutableStateOf(false) }
    val titleColor: Color by animateColorAsState(
        targetValue = if (isInFocus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        animationSpec = tween(animationDuration)
    )

    Card(
        onClick = { onDeckChosen(deck) },
        modifier = Modifier.testTag(DECK_TAG).padding(smallDp).fillMaxWidth()
            .onPointerEvent(PointerEventType.Enter) { isInFocus = true }
            .onPointerEvent(PointerEventType.Exit) { isInFocus = false },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = tinyDp
        ),
    ) {
        Row(
            modifier = Modifier.drawLeftBorder(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = deck.name,
                modifier = Modifier.weight(1f).padding(start = midDp),
                color = titleColor,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.width(midDp))
            Text(
                text = "${deck.size} ${if (deck.size != 1) "questions" else "question"}",
                modifier = Modifier.padding(midDp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
internal fun QuestionCard(question: String) {
    Card(
        modifier = Modifier.padding(smallDp).fillMaxWidth()
            .heightIn(preferredHeight.first, preferredHeight.second),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = tinyDp
        )
    ) {
        Text(
            text = question,
            modifier = Modifier.padding(midDp).wrapContentHeight(Alignment.CenterVertically),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun QuestionSummaryCard(
    index: Int,
    question: Question,
    onQuestionChosen: (Pair<Int, Question>) -> Unit
) {
    var isInFocus by remember { mutableStateOf(false) }
    val titleColor: Color by animateColorAsState(
        targetValue = if (isInFocus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        animationSpec = tween(animationDuration)
    )

    Card(
        onClick = { onQuestionChosen(index to question) },
        modifier = Modifier.testTag(QUEST_TAG).padding(smallDp).fillMaxWidth()
            .onPointerEvent(PointerEventType.Enter) { isInFocus = true }
            .onPointerEvent(PointerEventType.Exit) { isInFocus = false },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = tinyDp
        )
    ) {
        Row(
            modifier = Modifier.drawLeftBorder(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Q$index",
                modifier = Modifier.padding(start = midDp),
                color = titleColor,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = question.content,
                modifier = Modifier.padding(midDp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun AnswerCard(
    answer: String,
    quizMode: QuizMode,
    isCorrectAnswer: Boolean,
    isChosen: Boolean,
    onAnswerChosen: (String) -> Unit
) {
    Card(
        onClick = { onAnswerChosen(answer) },
        modifier = Modifier.testTag(ANSWER_TAG).padding(smallDp).fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            disabledContainerColor = when (quizMode) {
                QuizMode.CHECKED, QuizMode.CHOSEN -> if (isCorrectAnswer) {
                    MaterialTheme.colorScheme.primary
                } else if (isChosen) {
                    MaterialTheme.colorScheme.error
                } else MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.secondary
            },
            contentColor = MaterialTheme.colorScheme.onSecondary,
            disabledContentColor = MaterialTheme.colorScheme.onSecondary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = tinyDp
        ),
        enabled = quizMode == QuizMode.STUDYING || quizMode == QuizMode.WAITING
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = answer,
                // weight forces long texts to not clip Icon
                modifier = Modifier.weight(1f).padding(midDp),
                style = MaterialTheme.typography.bodyLarge
            )
            if (isChosen) {
                Icon(
                    painter = painterResource(if (isCorrectAnswer) Res.drawable.correct else Res.drawable.wrong),
                    contentDescription = if (isCorrectAnswer) CORRECT_DESCRIPTION else WRONG_DESCRIPTION,
                    modifier = Modifier
                        .padding(0.dp, smallDp, smallDp, smallDp)
                        .requiredSize(iconSize),
                    tint = MaterialTheme.colorScheme.onSurface
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
            for ((i, opt) in q2.optionalAnswers.withIndex()) {
                AnswerCard(opt, QuizMode.STUDYING, i == 2, isChosen=false) {}
            }
        }
    }
}

@Preview
@Composable
private fun QuestionAndAnswersCardInQuizModePreview() {
    SelfQuestTheme {
        Column {
            QuestionCard(q2.content)
            for ((i, opt) in q2.optionalAnswers.withIndex()) {
                AnswerCard(opt, QuizMode.WAITING, i == 2, isChosen=false) {}
            }
        }
    }
}

@Preview
@Composable
private fun QuestionSummaryCardPreview() {
    SelfQuestTheme {
        Column {
            QuestionSummaryCard(1, Question(1, "This a short question.",
                listOf("A", "B", "C", "D"), "C", null)) {}
            QuestionSummaryCard(
                2, Question(2, "This is an example of a very very very long multiline very long string question, which is very long.",
                listOf("A", "B", "C", "D"), "C", null)
            ) {}
        }
    }
}

@Preview
@Composable
private fun AnsweredCorrectInQuizModePreview() {
    SelfQuestTheme {
        Column {
            for ((i, opt) in q4.optionalAnswers.withIndex()) {
                AnswerCard(opt, QuizMode.CHOSEN, i == 2, i == 2) {}
            }
        }
    }
}

@Preview
@Composable
private fun AnsweredCorrectInStudyModePreview() {
    SelfQuestTheme {
        Column {
            for ((i, opt) in q4.optionalAnswers.withIndex()) {
                AnswerCard(opt, QuizMode.CHECKED, i == 2, i == 2) {}
            }
        }
    }
}

@Preview
@Composable
private fun AnsweredWrongPreview() {
    SelfQuestTheme {
        Column {
            for ((i, opt) in q4.optionalAnswers.withIndex()) {
                AnswerCard(opt, QuizMode.CHOSEN, i == 2, i == 0) {}
            }
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