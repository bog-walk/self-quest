package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bogwalk.ui.style.SelfQuestTheme
import dev.bogwalk.ui.style.cardPadding
import dev.bogwalk.ui.style.preferredWidth

@Composable
fun QuestionList(
    questions: List<String>,
    onQuestionClicked: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.widthIn(preferredWidth.first, preferredWidth.second).padding(cardPadding)
    ) {
        itemsIndexed(
            items = questions,
            key = { index: Int, q: String -> "$index$q" }
        ) { i, question ->
            QuestionSummaryCard(i + 1, question, onQuestionClicked)
        }
    }
}

@Composable
@Preview
private fun QuestionListPreview() {
    val questions = List(15) { "This is a fake question." }
    SelfQuestTheme {
        Box(Modifier.requiredHeight(preferredWidth.second)) {
            QuestionList(questions) {}
        }
    }
}