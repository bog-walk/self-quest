package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.bogwalk.models.q4
import dev.bogwalk.ui.style.*
import dev.bogwalk.ui.util.AnswerState

@Composable
fun QuestionCard(question: String) {
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QuestionSummaryCard(
    number: Int,
    question: String,
    onQuestionChosen: (String) -> Unit
) {
    Card(
        onClick = { onQuestionChosen(question) },
        modifier = Modifier.padding(cardPadding).fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground,
        elevation = cardElevation
    ) {
        Row(
            modifier = Modifier.drawBehind {
                drawLine(
                    color = SelfQuestColors.primary,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = CARD_STROKE
                )
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Q$number",
                modifier = Modifier.padding(start = innerPadding),
                style = MaterialTheme.typography.h5
            )
            Text(
                text = question,
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
fun AnswerCard(
    answer: String,
    answerState: AnswerState,
    isCorrectAnswer: Boolean,
    isChosen: Boolean = false,
    onAnswerChosen: (String) -> Unit
) {
    Card(
        onClick = { onAnswerChosen(answer) },
        modifier = Modifier.padding(cardPadding).fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        backgroundColor = when (answerState) {
            AnswerState.WAITING -> MaterialTheme.colors.secondary
            AnswerState.CHOSEN -> if (isCorrectAnswer) {
                MaterialTheme.colors.primary
            } else if (isChosen) {
                MaterialTheme.colors.error
            } else MaterialTheme.colors.secondary
        },
        contentColor = MaterialTheme.colors.onSecondary,
        elevation = cardElevation
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
                    contentDescription = null,
                    modifier = Modifier.padding(0.dp, cardPadding, cardPadding, cardPadding)
                        .requiredSize(iconSize),
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@Preview
@Composable
private fun QuestionAndAnswersCardPreview() {
    SelfQuestTheme {
        Column {
            QuestionCard(q4.content)
            AnswerCard(q4.optionalAnswers[0], AnswerState.WAITING, false) {}
            AnswerCard(q4.optionalAnswers[1], AnswerState.WAITING, false) {}
            AnswerCard(q4.optionalAnswers[2], AnswerState.WAITING, true) {}
            AnswerCard(q4.optionalAnswers[3], AnswerState.WAITING, false) {}
        }
    }
}

@Preview
@Composable
private fun QuestionSummaryCardPreview() {
    SelfQuestTheme {
        Column {
            QuestionSummaryCard(1, "This a short question.") {}
            QuestionSummaryCard(
                2,
                "This is an example of a very very very long multiline very long string question, which is very long."
            ) {}
        }
    }
}

@Preview
@Composable
private fun AnsweredCorrectPreview() {
    SelfQuestTheme {
        Column {
            AnswerCard(q4.optionalAnswers[0], AnswerState.CHOSEN, false) {}
            AnswerCard(q4.optionalAnswers[1], AnswerState.CHOSEN, false) {}
            AnswerCard(q4.optionalAnswers[2], AnswerState.CHOSEN, isCorrectAnswer = true, isChosen = true) {}
            AnswerCard(q4.optionalAnswers[3], AnswerState.CHOSEN, false) {}
        }
    }
}

@Preview
@Composable
private fun AnsweredWrongPreview() {
    SelfQuestTheme {
        Column {
            AnswerCard(q4.optionalAnswers[0], AnswerState.CHOSEN, isCorrectAnswer = false, isChosen = true) {}
            AnswerCard(q4.optionalAnswers[1], AnswerState.CHOSEN, false) {}
            AnswerCard(q4.optionalAnswers[2], AnswerState.CHOSEN, isCorrectAnswer = true) {}
            AnswerCard(q4.optionalAnswers[3], AnswerState.CHOSEN, false) {}
        }
    }
}

@Preview
@Composable
private fun VeryLongAnswerPreview() {
    val ans = "This is an example of a very very very long multiline very long string answer, which is very long"
    SelfQuestTheme {
        Column {
            AnswerCard(ans, AnswerState.WAITING, false) {}
            AnswerCard(ans, AnswerState.CHOSEN, isCorrectAnswer = true, isChosen = true) {}
        }
    }
}