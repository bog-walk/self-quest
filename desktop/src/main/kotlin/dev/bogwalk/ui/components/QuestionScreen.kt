package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.bogwalk.models.Question
import dev.bogwalk.models.q4
import dev.bogwalk.ui.style.SelfQuestTheme
import dev.bogwalk.ui.style.cardPadding
import dev.bogwalk.ui.style.preferredWidth
import dev.bogwalk.models.QuizMode

@Composable
fun QuestionScreen(
    question: Question,
    number: Int,
    total: Int,
    quizMode: QuizMode,
    chosenAnswer: String,
    onAnswerChosen: (String) -> Unit
) {
    Column(
        modifier = Modifier.widthIn(preferredWidth.first, preferredWidth.second).padding(cardPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SelfQuestHeader(
            header = "Question $number / $total",
            modifier = Modifier.align(Alignment.Start)
        )
        QuestionCard(question.content)
        for ((i, option) in listOf(
            question.optionalAnswer1, question.optionalAnswer2, question.optionalAnswer3, question.optionalAnswer4
        ).withIndex()) {
            key("${question.id}$i") {
                AnswerCard(
                    option, quizMode, option == question.expectedAnswer, option == chosenAnswer, onAnswerChosen
                )
            }
        }
    }
}

@Composable
internal fun SelfQuestHeader(
    header: String,
    modifier: Modifier
) {
    Text(
        text = header,
        modifier = modifier.padding(start = cardPadding),
        style = MaterialTheme.typography.h4
    )
}

@Composable
@Preview
private fun QuestionScreenPreview() {
    SelfQuestTheme {
        QuestionScreen(q4, 2, 5, QuizMode.WAITING, "") {}
    }
}

@Composable
@Preview
private fun QuestionScreenExtremesPreview() {
    val q = Question(1, "?".repeat(256), "A".repeat(128), "B", "C", "D", "C")
    SelfQuestTheme {
        QuestionScreen(q, 2, 5, QuizMode.WAITING, "") {}
    }
}