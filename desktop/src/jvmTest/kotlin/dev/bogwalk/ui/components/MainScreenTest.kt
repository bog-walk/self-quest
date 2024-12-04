package dev.bogwalk.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import dev.bogwalk.models.Deck
import dev.bogwalk.models.MainState
import dev.bogwalk.models.Question
import dev.bogwalk.models.QuizMode
import org.junit.Rule
import kotlin.test.Test

internal class MainScreenTest {
    private val question = Question(
        1, "This is a question",
        listOf("A", "B", "C", "D"), "C", null
    )

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `VerticalMenu & back arrow not displayed on MainScreen if not studying`() {
        val mode = mutableStateOf(QuizMode.WAITING)
        val chosen = mutableStateOf("")
        composeTestRule.setContent {
            MainScreen(false, MainState.IN_QUESTION, mode.value, 2 to 10, {}, {}, {}, {}, {}) {
                QuestionScreen(question, 2, 10, MainState.IN_QUESTION, mode.value, chosen.value, {}) {}
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
        val order = mutableStateOf(1 to 10)
        composeTestRule.setContent {
            MainScreen(false, MainState.IN_QUESTION, QuizMode.STUDYING, order.value, {}, {}, {}, {}, {}) {
                QuestionScreen(
                    question, order.value.first, order.value.second, MainState.IN_QUESTION, QuizMode.STUDYING, "", {}
                ) {}
            }
        }

        composeTestRule
            .onNodeWithTag(BACK_TAG).assertIsNotEnabled()
        composeTestRule
            .onNodeWithTag(FORWARD_TAG).assertIsEnabled()
        // vertical menu present when studying
        composeTestRule
            .onNodeWithTag(VERTICAL_TAG).assertExists()

        order.value = 5 to 10
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(BACK_TAG).assertIsEnabled()
        composeTestRule
            .onNodeWithTag(FORWARD_TAG).assertIsEnabled()

        order.value = 10 to 10
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(BACK_TAG).assertIsEnabled()
        composeTestRule
            .onNodeWithTag(FORWARD_TAG).assertIsNotEnabled()
    }

    @Test
    fun `VerticalMenu not displayed if loading data on MainScreen`() {
        val loading = mutableStateOf(true)
        composeTestRule.setContent {
            MainScreen(loading.value, MainState.ALL_DECKS, QuizMode.STUDYING, 0 to 0, {}, {}, {}, {}, {}) {
                DeckList(listOf(Deck(1, "New", 5))) {}
            }
        }

        composeTestRule
            .onNodeWithTag(VERTICAL_TAG).assertDoesNotExist()
        composeTestRule
            .onNodeWithText(LOADING_TEXT).assertExists()
        composeTestRule
            .onNodeWithTag(DECK_TAG).assertDoesNotExist()

        loading.value = false
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(VERTICAL_TAG).assertExists()
        composeTestRule
            .onNodeWithText(LOADING_TEXT).assertDoesNotExist()
        composeTestRule
            .onNodeWithTag(DECK_TAG).assertExists()
    }
}