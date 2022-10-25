package dev.bogwalk.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import dev.bogwalk.models.Question
import dev.bogwalk.ui.style.BACK_TAG
import dev.bogwalk.ui.style.FORWARD_TAG
import dev.bogwalk.ui.style.VERTICAL_TAG
import dev.bogwalk.models.MainState
import dev.bogwalk.models.QuizMode
import org.junit.Rule
import kotlin.test.Test

internal class MainScreenTest {
    private val question = Question(
        1, "This is a question", "A", "B", "C", "D", "C"
    )

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `VerticalMenu & back arrow not displayed on MainScreen if not studying`() {
        val mode = mutableStateOf(QuizMode.WAITING)
        val chosen = mutableStateOf("")
        composeTestRule.setContent {
            MainScreen(MainState.IN_QUESTION, mode.value, 1 to 10, {}, {}, {}, {}, {}) {
                QuestionScreen(question, 2, 10, mode.value, chosen.value) {}
            }
        }

        composeTestRule
            .onNodeWithTag(VERTICAL_TAG).assertDoesNotExist()
        composeTestRule
            .onNodeWithTag(BACK_TAG).assertDoesNotExist()
        composeTestRule
            .onNodeWithTag(FORWARD_TAG).assertIsNotEnabled()

        mode.value = QuizMode.CHOSEN
        chosen.value = "C"
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(VERTICAL_TAG).assertDoesNotExist()
        composeTestRule
            .onNodeWithTag(BACK_TAG).assertDoesNotExist()
        composeTestRule
            .onNodeWithTag(FORWARD_TAG).assertIsEnabled()
    }

    @Test
    fun `both arrows enabled if Question has previous and next`() {
        val order = mutableStateOf(9 to 10)
        composeTestRule.setContent {
            MainScreen(MainState.IN_QUESTION, QuizMode.STUDYING, order.value, {}, {}, {}, {}, {}) {
                QuestionScreen(question, order.value.first, order.value.second, QuizMode.STUDYING, "") {}
            }
        }

        composeTestRule
            .onNodeWithTag(BACK_TAG).assertIsEnabled()
        composeTestRule
            .onNodeWithTag(FORWARD_TAG).assertIsEnabled()
        // vertical menu present when studying
        composeTestRule
            .onNodeWithTag(VERTICAL_TAG).assertExists()

        order.value = 10 to 10
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(BACK_TAG).assertIsEnabled()
        composeTestRule
            .onNodeWithTag(FORWARD_TAG).assertIsNotEnabled()
    }
}