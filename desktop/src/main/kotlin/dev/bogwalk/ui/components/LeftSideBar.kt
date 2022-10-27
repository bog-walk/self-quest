package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import dev.bogwalk.models.MainState
import dev.bogwalk.models.QuizMode
import dev.bogwalk.ui.style.*

@Composable
fun LeftSideBar(
    screenState: MainState,
    title: String?,
    onBackButtonClicked: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = Modifier.requiredWidth(sidebarWidth)
            .fillMaxHeight()
            .drawBehind {
                drawLine(
                    color = SelfQuestColors.primary,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = SIDEBAR_STROKE
                )
            }
    ) {
        Box(
            modifier = Modifier
                .requiredHeight(topBoxHeight)
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary)
        ) {
            // need to provide a way back from adding a new deck without saving (cancel button?)
            if (title != null || screenState == MainState.UPDATING_DECK) {
                ArrowButton(
                    modifier = Modifier.align(Alignment.TopStart),
                    onButtonClick = onBackButtonClicked
                )
            }
            title?.let {
                Text(
                    text = title,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(cardPadding),
                    textAlign = TextAlign.Right,
                    style = MaterialTheme.typography.h4  // textAlign is left
                )
            }
        }
        content()
    }
}

@Composable
@Preview
private fun LeftSideBarEmptyPreview() {
    SelfQuestTheme {
        Box(Modifier.requiredSize(preferredWidth.second)) {
            LeftSideBar(MainState.ALL_DECKS, null, {}) {}
        }
    }
}

@Composable
@Preview
private fun LeftSideBarWithArrowPreview() {
    SelfQuestTheme {
        Box(Modifier.requiredSize(preferredWidth.second)) {
            LeftSideBar(MainState.UPDATING_DECK, null, {}) {}
        }
    }
}

@Composable
@Preview
private fun LeftSideBarDeckOverviewPreview() {
    SelfQuestTheme {
        Box(Modifier.requiredSize(preferredWidth.second)) {
            LeftSideBar(MainState.DECK_OVERVIEW, "Equine", {}) { QuizModeSwitch(QuizMode.STUDYING) {} }
        }
    }
}

@Composable
@Preview
private fun LeftSideBarDeckOverviewLongWithBreakPreview() {
    SelfQuestTheme {
        Box(Modifier.requiredSize(preferredWidth.second)) {
            LeftSideBar(MainState.DECK_OVERVIEW, "Anatomy & Physiology", {}) { QuizModeSwitch(QuizMode.STUDYING) {} }
        }
    }
}

@Composable
@Preview
private fun LeftSideBarInQuestionLongWithoutBreakPreview() {
    SelfQuestTheme {
        Box(Modifier.requiredSize(preferredWidth.second)) {
            LeftSideBar(MainState.IN_QUESTION, "Endocrinology", {}) { QuizModeSwitch(QuizMode.WAITING) {} }
        }
    }
}