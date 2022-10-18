package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.bogwalk.models.q4
import dev.bogwalk.ui.style.SelfQuestTheme
import dev.bogwalk.ui.style.cardPadding
import dev.bogwalk.ui.style.preferredWidth
import dev.bogwalk.ui.util.AnswerState

@Composable
fun QuestionScreen(
    question: String,
    number: Int,
    total: Int,
    answerState: AnswerState,
    options: List<String>,
    expected: String,
    onAnswerChosen: (String) -> Unit
) {
    Column(
        modifier = Modifier.widthIn(preferredWidth.first, preferredWidth.second).padding(cardPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Question $number / $total",
            modifier = Modifier.align(Alignment.Start).padding(start = cardPadding),
            style = MaterialTheme.typography.h4
        )
        QuestionCard(question)
        for ((i, option) in options.withIndex()) {
            key("$i $option") {
                AnswerCard(option, answerState, option == expected, onAnswerChosen = onAnswerChosen)
            }
        }
    }
}

@Composable
@Preview
private fun QuestionScreenPreview() {
    SelfQuestTheme {
        QuestionScreen(
            q4.content, 2, 5, AnswerState.WAITING, q4.optionalAnswers, q4.expectedAnswer
        ) {}
    }
}