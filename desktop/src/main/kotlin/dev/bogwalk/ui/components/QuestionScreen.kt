package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import dev.bogwalk.models.*
import dev.bogwalk.ui.style.*

internal const val QUESTION = "Question"
internal const val REVIEW = "Review"
internal const val REFERENCES = "References:"

@Composable
internal fun QuestionScreen(
    question: Question,
    number: Int,
    total: Int,
    screenState: MainState,
    quizMode: QuizMode,
    chosenAnswer: String,
    onTabSelected: () -> Unit,
    onAnswerChosen: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var tabIndex by remember { mutableStateOf(if (screenState == MainState.IN_QUESTION) 0 else 1) }
    // this should maybe be extracted to an enum/sealed class
    val tabs = listOf(QUESTION, REVIEW)

    Column(
        modifier = Modifier
            .widthIn(preferredWidth.first, preferredWidth.second)
            .padding(smallDp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TabRow(
            // workaround to ensure switch to question tab in the event quiz mode toggled while in review
            selectedTabIndex = if (screenState == MainState.IN_QUESTION) {
                tabIndex = 0
                0
            } else 1,
            modifier = Modifier.padding(bottom = midDp),
            backgroundColor = Color.Transparent,
            indicator = { TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(it[tabIndex]),
                height = TabRowDefaults.IndicatorHeight * 2f,
                color = MaterialTheme.colors.primary
            ) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = {
                        tabIndex = index
                        onTabSelected()
                    },
                    modifier = Modifier.testTag(title),
                    enabled = quizMode == QuizMode.STUDYING || quizMode == QuizMode.CHECKED,
                    text = { Text(title) }
                )
            }
        }
        SelfQuestHeader(
            header = "Question $number / $total",
            modifier = Modifier.align(Alignment.Start)
        )
        when (tabIndex) {
            0 -> {
                QuestionCard(question.content)
                for ((i, option) in question.optionalAnswers.withIndex()) {
                    key("${question.id}$i") {
                        AnswerCard(
                            option, quizMode, option == question.expectedAnswer,
                            option == chosenAnswer, onAnswerChosen
                        )
                    }
                }
            }
            1 -> {
                question.review?.let { review ->
                    ReviewScreen(review)
                }
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
        modifier = modifier.padding(start = midDp),
        style = MaterialTheme.typography.h4
    )
}

@Composable
private fun ReviewScreen(
    review: Review
) {
    val uriHandler = LocalUriHandler.current

    if (review.content.isNotEmpty()) {
        Text(
            text = review.content,
            Modifier.testTag(CONTENT_TAG).padding(midDp).fillMaxWidth(),
            style = MaterialTheme.typography.body1
        )
    }
    if (review.references.isNotEmpty()) {
        Text(
            REFERENCES,
            Modifier.padding(vertical = smallDp, horizontal = midDp).fillMaxWidth(),
            style = MaterialTheme.typography.h6
        )
        review.references.forEachIndexed { i, (name, url) ->
            key("$i$name") {
                Row(
                    modifier = Modifier
                        .padding(vertical = smallDp, horizontal = midDp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(STUDY_ICON),
                        STUDY_DESCRIPTION,
                        Modifier.requiredSize(iconSize),
                        MaterialTheme.colors.onSurface
                    )
                    Spacer(Modifier.width(midDp))
                    ClickableText(
                        text = AnnotatedString(name, SpanStyle(color = MaterialTheme.colors.primary)),
                        style = MaterialTheme.typography.body1
                    ) {
                        uriHandler.openUri(url)
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun QuestionScreenPreview() {
    SelfQuestTheme {
        QuestionScreen(q4, 2, 5, MainState.IN_QUESTION, QuizMode.STUDYING, "", {}) {}
    }
}

@Composable
@Preview
private fun QuestionScreenNoReviewPreview() {
    SelfQuestTheme {
        QuestionScreen(q3, 2, 5, MainState.IN_REVIEW, QuizMode.STUDYING, "", {}) {}
    }
}

@Composable
@Preview
private fun QuestionScreenInQuizPreview() {
    SelfQuestTheme {
        QuestionScreen(q4, 2, 5, MainState.IN_QUESTION, QuizMode.WAITING, "", {}) {}
    }
}

@Composable
@Preview
private fun QuestionScreenExtremesPreview() {
    val q = Question(1, "?".repeat(256),
        listOf("A".repeat(128), "B", "C", "D"), "C",
        Review(review.repeat(9), references)
    )
    SelfQuestTheme {
        QuestionScreen(q, 2, 5, MainState.IN_QUESTION, QuizMode.STUDYING, "", {}) {}
    }
}