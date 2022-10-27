package dev.bogwalk.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.ui.style.CORRECT_DESCRIPTION
import dev.bogwalk.ui.style.WRONG_DESCRIPTION
import dev.bogwalk.models.QuizMode
import dev.bogwalk.ui.style.ANSWER_TAG
import org.junit.Rule
import kotlin.test.Test

internal class AnswerCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `AnswerCard tick Icon appears if chosen (quiz mode) correctly`() {
        val mode = mutableStateOf(QuizMode.WAITING)
        val isChosen = mutableStateOf(false)
        composeTestRule.setContent {
            AnswerCard("A", mode.value, true, isChosen.value) {}
        }

        // printToLog() still not implemented? how to view siblings?
        composeTestRule
            .onNodeWithText("A").assertExists()
        composeTestRule
            .onNodeWithContentDescription(CORRECT_DESCRIPTION).assertDoesNotExist()

        mode.value = QuizMode.CHOSEN
        isChosen.value = true
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription(CORRECT_DESCRIPTION).assertExists()
    }

    @Test
    fun `AnswerCard tick Icon appears if checked (study mode) correctly`() {
        val mode = mutableStateOf(QuizMode.STUDYING)
        val isChosen = mutableStateOf(false)
        composeTestRule.setContent {
            AnswerCard("A", mode.value, true, isChosen.value) {}
        }

        // printToLog() still not implemented? how to view siblings?
        composeTestRule
            .onNodeWithText("A").assertExists()
        composeTestRule
            .onNodeWithContentDescription(CORRECT_DESCRIPTION).assertDoesNotExist()

        mode.value = QuizMode.CHECKED
        isChosen.value = true
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription(CORRECT_DESCRIPTION).assertExists()
    }

    @Test
    fun `AnswerCard cross Icon appears if chosen (quiz mode) incorrectly`() {
        val mode = mutableStateOf(QuizMode.WAITING)
        val isChosen = mutableStateOf(false)
        composeTestRule.setContent {
            AnswerCard("A", mode.value, false, isChosen.value) {}
        }

        composeTestRule
            .onNodeWithText("A").assertExists()
        composeTestRule
            .onNodeWithContentDescription(WRONG_DESCRIPTION).assertDoesNotExist()

        mode.value = QuizMode.CHOSEN
        isChosen.value = true
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription(WRONG_DESCRIPTION).assertExists()
    }

    @Test
    fun `AnswerCard cross Icon appears if checked (study mode) incorrectly`() {
        val mode = mutableStateOf(QuizMode.STUDYING)
        val isChosen = mutableStateOf(false)
        composeTestRule.setContent {
            AnswerCard("A", mode.value, false, isChosen.value) {}
        }

        composeTestRule
            .onNodeWithText("A").assertExists()
        composeTestRule
            .onNodeWithContentDescription(WRONG_DESCRIPTION).assertDoesNotExist()

        mode.value = QuizMode.CHECKED
        isChosen.value = true
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription(WRONG_DESCRIPTION).assertExists()
    }

    @Test
    fun `AnswerCard disabled once chosen or checked`() {
        val mode = mutableStateOf(QuizMode.STUDYING)
        val isChosen = mutableStateOf(false)
        composeTestRule.setContent {
            AnswerCard("A", mode.value, false, isChosen.value) {}
        }

        composeTestRule
            .onNodeWithTag(ANSWER_TAG).assertIsEnabled()

        mode.value = QuizMode.CHECKED
        isChosen.value = true
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(ANSWER_TAG).assertIsNotEnabled()

        mode.value = QuizMode.WAITING
        isChosen.value = false
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(ANSWER_TAG).assertIsEnabled()

        mode.value = QuizMode.CHOSEN
        isChosen.value = true
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(ANSWER_TAG).assertIsNotEnabled()
    }
}