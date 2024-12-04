package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import dev.bogwalk.models.questionStorage
import dev.bogwalk.ui.style.SelfQuestScrollBar
import dev.bogwalk.ui.style.SelfQuestTheme
import dev.bogwalk.ui.style.smallDp
import dev.bogwalk.ui.style.preferredWidth

@Composable
fun DeckList(
    decks: List<Deck>,
    onDeckClicked: (Deck) -> Unit
) {
    SelfQuestLazyList {
        itemsIndexed(
            items = decks,
            key = { index: Int, d: Deck -> "$index${d.id}" }
        ) { _, deck ->
            DeckCard(deck, onDeckClicked)
        }
    }
}

@Composable
internal fun QuestionList(
    questions: List<Question>,
    onQuestionClicked: (Pair<Int, Question>) -> Unit
) {
    SelfQuestLazyList {
        itemsIndexed(
            items = questions,
            key = { index: Int, q: Question -> "$index${q.id}" }
        ) { i, question ->
            QuestionSummaryCard(i + 1, question, onQuestionClicked)
        }
    }
}

@Composable
private fun SelfQuestLazyList(
    content: LazyListScope.() -> Unit
) {
    val scrollState = rememberLazyListState()

    Box(
        modifier = Modifier.widthIn(preferredWidth.first, preferredWidth.second),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(smallDp)
        ) {
            content()
        }
        VerticalScrollbar(
            adapter = ScrollbarAdapter(scrollState),
            modifier = Modifier.align(Alignment.CenterEnd),
            style = SelfQuestScrollBar
        )
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