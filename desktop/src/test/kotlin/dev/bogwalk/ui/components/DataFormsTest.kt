package dev.bogwalk.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import dev.bogwalk.models.Review
import dev.bogwalk.models.references
import dev.bogwalk.ui.style.*
import org.junit.Rule
import kotlin.test.Test

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

        composeTestRule
            .onNodeWithTag(NAME_TAG).performTextInput("Title")
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsEnabled()
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

        composeTestRule
            .onNodeWithTag(NAME_TAG).performTextInput("Updated")
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsEnabled()
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
                "D", "C", null
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

    @Test
    fun `QuestionDataForm save not enabled until a radio button selected and all fields entered`() {
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

        composeTestRule.onNodeWithTag(CONTENT_TAG).performTextInput("Fake question.")
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag("Answer 1").performTextInput("A")
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag("Answer 2").performTextInput("B")
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag("Answer 3").performTextInput("C")
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag("Answer 4").performTextInput("D")
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
        composeTestRule.onAllNodesWithTag(RADIO_TAG)[0].performClick()
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsEnabled()
    }

    @Test
    fun `ReviewDataForm loads with only minimal content`() {
        composeTestRule.setContent {
            ReviewDataForm(null) {}
        }

        composeTestRule
            .onNodeWithText("$EDIT_HEADER review").assertExists()
        composeTestRule
            .onNodeWithTag(CONTENT_TAG).assertExists()
        composeTestRule
            .onAllNodesWithTag(LINK_TAG).assertCountEquals(0)
        composeTestRule
            .onNodeWithText(ADD_LINK).assertIsEnabled()
        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsNotEnabled()

        composeTestRule.setContent {
            ReviewDataForm(Review("Explanation", emptyList())) {}
        }
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(CONTENT_TAG).assertExists()
        composeTestRule
            .onAllNodesWithTag(LINK_TAG).assertCountEquals(0)
        composeTestRule
            .onNodeWithText(ADD_LINK).assertIsEnabled()
        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsEnabled()

        composeTestRule.setContent {
            ReviewDataForm(Review("", listOf("Link Name" to "Link"))) {}
        }
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(CONTENT_TAG).assertExists()
        composeTestRule
            .onAllNodesWithTag(LINK_TAG).assertCountEquals(2)
        composeTestRule
            .onNodeWithText(ADD_LINK).assertIsEnabled()
        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsEnabled()
    }

    @Test
    fun `add link button adds TextFields to ReviewDataForm`() {
        composeTestRule.setContent {
            ReviewDataForm(Review("", references)) {}
        }

        composeTestRule
            .onNodeWithText("$EDIT_HEADER review").assertExists()
        composeTestRule
            .onNodeWithTag(CONTENT_TAG).assertExists()
        composeTestRule
            .onAllNodesWithTag(LINK_TAG).assertCountEquals(6)
        composeTestRule
            .onNodeWithText(ADD_LINK).assertIsEnabled()
        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsEnabled()

        composeTestRule
            .onNodeWithText(ADD_LINK).performClick()
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(CONTENT_TAG).assertExists()
        composeTestRule
            .onAllNodesWithTag(LINK_TAG).assertCountEquals(8)
        composeTestRule
            .onNodeWithTag(SAVE_TAG).assertIsEnabled()
    }
}