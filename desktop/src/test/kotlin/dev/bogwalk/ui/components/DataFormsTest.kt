package dev.bogwalk.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import dev.bogwalk.ui.style.*
import org.junit.Rule
import kotlin.test.Test

// Unable to run tests fully due to NotImplementedError (upgrade compose to 1.2.+)
internal class DataFormsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `DeckDataForm save button not enabled until name provided`() {
        composeTestRule.setContent {
            DeckDataForm(null) {}
        }

        composeTestRule
            .onNodeWithText("$ADD_HEADER collection").assertExists()
        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
        /*
        composeTestRule
            .onNodeWithTag(NAME_TAG).performTextInput("Title")
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsEnabled()*/
    }

    @Test
    fun `DeckDataForm save button not enabled until name differs from original`() {
        composeTestRule.setContent {
            DeckDataForm(Deck(1, "Original", 2)) {}
        }

        composeTestRule
            .onNodeWithText("$EDIT_HEADER collection").assertExists()
        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
        /*
        composeTestRule
            .onNodeWithTag(NAME_TAG).performTextInput("Updated")
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsEnabled()*/
    }

    @Test
    fun `QuestionDataForm loads with all radio buttons and save disabled if new question`() {
        composeTestRule.setContent {
            QuestionDataForm(null) {}
        }

        composeTestRule
            .onNodeWithText("$ADD_HEADER question").assertExists()
        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
        composeTestRule
            .onAllNodesWithTag(RADIO_TAG)
            .assertAll(isNotSelected() and isNotEnabled())
    }

    @Test
    fun `QuestionDataForm loads with one selected radio button and save enabled if editing`() {
        composeTestRule.setContent {
            QuestionDataForm(Question(
                1, "Question?", "A", "B", "C",
                "D", "C"
            )) {}
        }

        composeTestRule
            .onNodeWithText("$EDIT_HEADER question").assertExists()
        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsEnabled()
        composeTestRule
            .onAllNodesWithTag(RADIO_TAG)
            .assertAll(isEnabled())
            .filter(isSelected())
            .assertCountEquals(1)
    }
}