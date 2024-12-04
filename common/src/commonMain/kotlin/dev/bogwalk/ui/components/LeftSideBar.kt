package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import dev.bogwalk.models.MainState
import dev.bogwalk.models.QuizMode
import dev.bogwalk.ui.style.*

private const val SIDEBAR_STROKE = 1f
const val BACK_ALL_DESCRIPTION = "Back to all collections"
internal const val BACK_ONE_DESCRIPTION = "Back to collection overview"
const val BACK_FORM_DESCRIPTION = "Exit form"

@Composable
fun LeftSideBar(
    screenState: MainState,
    title: String?,
    onBackButtonClicked: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = Modifier.requiredWidth(midWidth)
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
                .requiredHeight(midHeight)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            if (title != null || screenState == MainState.UPDATING_DECK) {
                ArrowButton(
                    modifier = Modifier.align(Alignment.TopStart),
                    description = when (screenState) {
                        MainState.DECK_OVERVIEW -> BACK_ALL_DESCRIPTION
                        MainState.IN_QUESTION, MainState.IN_REVIEW -> BACK_ONE_DESCRIPTION
                        else -> BACK_FORM_DESCRIPTION
                    },
                    onButtonClick = onBackButtonClicked
                )
            }
            title?.let {
                Text(
                    text = title,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(smallDp),
                    textAlign = TextAlign.Right,
                    style = MaterialTheme.typography.headlineMedium
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