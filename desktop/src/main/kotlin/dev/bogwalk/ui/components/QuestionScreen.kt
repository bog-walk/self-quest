package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
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
import dev.bogwalk.models.Question
import dev.bogwalk.models.q4
import dev.bogwalk.models.QuizMode
import dev.bogwalk.ui.style.*

@Composable
fun QuestionScreen(
    question: Question,
    number: Int,
    total: Int,
    quizMode: QuizMode,
    chosenAnswer: String,
    onAnswerChosen: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var tabIndex by remember { mutableStateOf(0) }
    // this should maybe be extracted to an enum/sealed class
    val tabs = listOf(QUESTION, REVIEW)

    Column(
        modifier = Modifier
            .widthIn(preferredWidth.first, preferredWidth.second)
            .padding(cardPadding)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TabRow(
            // workaround to ensure switch to question tab in the event quiz mode toggled while in review
            selectedTabIndex = if (quizMode == QuizMode.WAITING) {
                tabIndex = 0
                0
            } else tabIndex,
            modifier = Modifier.padding(bottom = innerPadding),
            backgroundColor = Color.Transparent,
            indicator = { TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(it[tabIndex]),
                height = TabRowDefaults.IndicatorHeight * 2f,
                color = MaterialTheme.colors.primary
            ) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    // works, so try without this?
                    selected = quizMode == QuizMode.WAITING && index == 0 ||
                            quizMode != QuizMode.WAITING && tabIndex == index,
                    onClick = { tabIndex = index },
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
            1 -> {
                val uriHandler = LocalUriHandler.current

                Text(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce volutpat luctus interdum. Fusce malesuada eget magna molestie volutpat. Suspendisse facilisis, arcu at condimentum dapibus, diam tortor pellentesque diam, ut convallis augue ligula vitae eros. Sed sollicitudin ipsum erat. Suspendisse tincidunt magna ipsum, quis fringilla enim blandit quis. Nam nibh lorem, egestas vel magna at, vestibulum euismod tortor. Duis neque eros, euismod vel mi vel, aliquet maximus diam. Praesent vel tempor enim, sit amet imperdiet nisi. Nunc viverra augue in consectetur finibus. Fusce a ornare nibh. Vivamus porta ullamcorper lorem eu egestas. Suspendisse egestas non eros vitae tincidunt. Aenean risus erat, congue non commodo eget, hendrerit varius felis. Integer ac arcu ligula.",
                    Modifier.padding(innerPadding),
                    style = MaterialTheme.typography.body1
                )
                Text(REFERENCES, Modifier.padding(vertical = cardPadding, horizontal = innerPadding).fillMaxWidth(),
                    style = MaterialTheme.typography.h6)
                listOf(
                    "Kotlin" to "https://kotlinlang.org/",
                    "JetBrains" to "https://www.jetbrains.com/",
                    "GitHub" to "https://github.com/"
                ).forEachIndexed { i, (name, url) ->
                    key("$i$name") {
                        Row(
                            modifier = Modifier.padding(vertical = cardPadding, horizontal = innerPadding).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painterResource(STUDY_ICON),
                                STUDY_DESCRIPTION,
                                Modifier.requiredSize(iconSize),
                                MaterialTheme.colors.onSurface
                            )
                            Spacer(Modifier.width(innerPadding))
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
    }
}

@Composable
internal fun SelfQuestHeader(
    header: String,
    modifier: Modifier
) {
    Text(
        text = header,
        modifier = modifier.padding(start = innerPadding),
        style = MaterialTheme.typography.h4
    )
}

@Composable
@Preview
private fun QuestionScreenPreview() {
    SelfQuestTheme {
        QuestionScreen(q4, 2, 5, QuizMode.STUDYING, "") {}
    }
}

@Composable
@Preview
private fun QuestionScreenInQuizPreview() {
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