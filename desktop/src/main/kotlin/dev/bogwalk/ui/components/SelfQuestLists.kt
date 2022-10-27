package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import dev.bogwalk.models.questionStorage
import dev.bogwalk.ui.style.SelfQuestTheme
import dev.bogwalk.ui.style.cardPadding
import dev.bogwalk.ui.style.preferredWidth

@Composable
fun DeckList(
    decks: List<Deck>,
    onDeckClicked: (Deck) -> Unit
) {
    LazyColumn(
        modifier = Modifier.widthIn(preferredWidth.first, preferredWidth.second).padding(cardPadding)
    ) {
        items(
            items = decks,
            key = { d: Deck -> d.id }
        ) { deck ->
            DeckCard(deck, onDeckClicked)
        }
    }
}

@Composable
fun QuestionList(
    questions: List<Question>,
    onQuestionClicked: (Pair<Int, Question>) -> Unit
) {
    LazyColumn(
        modifier = Modifier.widthIn(preferredWidth.first, preferredWidth.second).padding(cardPadding)
    ) {
        itemsIndexed(
            items = questions,
            key = { index: Int, q: Question -> "$index${q.id}" }
        ) { i, question ->
            QuestionSummaryCard(i + 1, question, onQuestionClicked)
        }
    }
}

@Composable
@Preview
private fun DeckListPreview() {
    val decks = List(15) { Deck(it + 1, "Title", 9) }

    SelfQuestTheme {
        Box(Modifier.requiredHeight(preferredWidth.second)) {
            DeckList(decks) {}
        }
    }
}

@Composable
@Preview
private fun QuestionListPreview() {
    SelfQuestTheme {
        Box(Modifier.requiredHeight(preferredWidth.second)) {
            QuestionList(questionStorage) {}
        }
    }
}