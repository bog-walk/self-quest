package dev.bogwalk.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.models.MainState
import dev.bogwalk.models.QuizMode
import dev.bogwalk.models.q4
import dev.bogwalk.ui.style.ANSWER_TAG
import dev.bogwalk.ui.style.QUESTION
import dev.bogwalk.ui.style.REFERENCES
import dev.bogwalk.ui.style.REVIEW
import org.junit.Rule
import kotlin.test.Test

internal class QuestionScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `QuestionScreen default loads quiz with question tab selected`() {
        val mode = mutableStateOf(QuizMode.WAITING)
        val chosen = mutableStateOf("")
        composeTestRule.setContent {
            QuestionScreen(q4, 2, 5, MainState.IN_QUESTION, mode.value, chosen.value, {}) {}
        }

        composeTestRule
            .onAllNodesWithTag(ANSWER_TAG).assertCountEquals(4)
        composeTestRule
            .onNodeWithTag(QUESTION).assertIsSelected()
        composeTestRule
            .onNodeWithTag(REVIEW).assert(isNotSelected() and isNotEnabled())

        mode.value = QuizMode.CHOSEN
        chosen.value = "A"
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(QUESTION).assertIsSelected()
        composeTestRule
            .onNodeWithTag(REVIEW).assertIsNotEnabled()
    }

    @Test
    fun `QuestionScreen in study mode allows review tab selection`() {
        val state = mutableStateOf(MainState.IN_QUESTION)
        val mode = mutableStateOf(QuizMode.STUDYING)
        val chosen = mutableStateOf("")
        composeTestRule.setContent {
            QuestionScreen(q4, 2, 5, state.value, mode.value, chosen.value, { state.value = MainState.IN_REVIEW }) {}
        }

        composeTestRule
            .onAllNodesWithTag(ANSWER_TAG).assertCountEquals(4)
        composeTestRule
            .onNodeWithTag(QUESTION).assertIsSelected()
        composeTestRule
            .onNodeWithTag(REVIEW).assert(isNotSelected() and isEnabled())

        mode.value = QuizMode.CHECKED
        chosen.value = "A"
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(QUESTION).assertIsSelected()
        composeTestRule
            .onNodeWithTag(REVIEW).assert(isNotSelected() and isEnabled())

        composeTestRule
            .onNodeWithTag(REVIEW).performClick()
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText(REFERENCES).assertExists()
        composeTestRule
            .onNodeWithTag(QUESTION).assert(isNotSelected() and isEnabled())
        composeTestRule
            .onNodeWithTag(REVIEW).assertIsSelected()
    }
}