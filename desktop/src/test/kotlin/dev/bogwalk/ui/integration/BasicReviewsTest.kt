package dev.bogwalk.ui.integration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.ui.SelfQuestApp
import dev.bogwalk.ui.style.*
import org.junit.Rule
import kotlin.test.Test

internal class BasicReviewsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // testing of Dialogs not fully supported?
    @Test
    fun `addition, editing, and deletion of a new Review`() {
        composeTestRule.setContent {
            SelfQuestApp(TestAppState())
        }

        // app loads with no saved decks
        composeTestRule.onNodeWithTag(BACK_TAG).assertDoesNotExist()
        composeTestRule.onAllNodesWithTag(DECK_TAG).assertCountEquals(0)
        composeTestRule.onNodeWithTag(VERTICAL_TAG).assertExists()
        composeTestRule.onNodeWithTag(ADD_TAG).assertIsEnabled()
        composeTestRule.onNodeWithTag(EDIT_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(DELETE_TAG).assertIsNotEnabled()

        // add a new deck
        composeTestRule.onNodeWithTag(ADD_TAG).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("$ADD_HEADER collection").assertExists()
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(BACK_TAG).assertExists()
        val name = "Test Collection"
        composeTestRule.onNodeWithTag(NAME_TAG).performTextInput(name)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsEnabled().performClick()
        composeTestRule.waitForIdle()

        // auto-navigates to deck overview
        composeTestRule.onNodeWithTag(BACK_TAG).assertExists()
        composeTestRule.onNodeWithText(name).assertExists()
        composeTestRule.onAllNodesWithTag(QUEST_TAG).assertCountEquals(0)
        composeTestRule.onNodeWithTag(VERTICAL_TAG).assertExists()
        composeTestRule.onNodeWithTag(ADD_TAG).assertIsEnabled()
        composeTestRule.onNodeWithTag(EDIT_TAG).assertIsEnabled()
        composeTestRule.onNodeWithTag(DELETE_TAG).assertIsEnabled()

        // add Question
        composeTestRule.onNodeWithTag(ADD_TAG).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("$ADD_HEADER question").assertExists()
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(BACK_TAG).assertExists()

        val question = "Fake question"
        composeTestRule.onNodeWithTag(CONTENT_TAG).performTextInput(question)
        composeTestRule.onNodeWithTag("Answer 1").performTextInput("A")
        composeTestRule.onNodeWithTag("Answer 2").performTextInput("B")
        composeTestRule.onNodeWithTag("Answer 3").performTextInput("C")
        composeTestRule.onNodeWithTag("Answer 4").performTextInput("D")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
        composeTestRule.onAllNodesWithTag(RADIO_TAG).assertCountEquals(4)
        composeTestRule.onAllNodesWithTag(RADIO_TAG)[0].performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsEnabled().performClick()
        composeTestRule.waitForIdle()

        // auto-navigates to question screen
        composeTestRule
            .onAllNodesWithTag(BACK_TAG).assertCountEquals(2)
            .filter(isEnabled()).assertCountEquals(1)
        composeTestRule.onNodeWithTag(FORWARD_TAG).assertExists().assertIsNotEnabled()
        composeTestRule.onNodeWithText(question).assertExists()
        composeTestRule.onAllNodesWithTag(ANSWER_TAG).assertCountEquals(4)
        composeTestRule.onNodeWithTag(VERTICAL_TAG).assertExists()
        composeTestRule.onNodeWithTag(ADD_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(EDIT_TAG).assertIsEnabled()
        composeTestRule.onNodeWithTag(DELETE_TAG).assertIsEnabled()

        // select review tab to show empty screen
        composeTestRule.onNodeWithTag(QUESTION).assertIsSelected()
        composeTestRule.onNodeWithTag(REVIEW).assertIsNotSelected().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Question 1 / 1").assertExists()
        composeTestRule.onNodeWithTag(CONTENT_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(VERTICAL_TAG).assertExists()
        composeTestRule.onNodeWithTag(ADD_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(EDIT_TAG).assertIsEnabled()
        composeTestRule.onNodeWithTag(DELETE_TAG).assertIsEnabled()

        // add review (via editing)
        composeTestRule.onNodeWithTag(EDIT_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("$EDIT_HEADER review").assertExists()
        composeTestRule.onNodeWithText(ADD_LINK).assertIsEnabled()
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsNotEnabled()

        // fill out form
        val review = listOf("Explanation", "Link Name", "Link")
        composeTestRule.onNodeWithTag(CONTENT_TAG).performTextInput(review[0])
        composeTestRule.onNodeWithText(ADD_LINK).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsEnabled()
        composeTestRule.onAllNodesWithTag(LINK_TAG).assertCountEquals(2)
        composeTestRule.onAllNodesWithTag(LINK_TAG)[0].performTextInput(review[1])
        composeTestRule.onAllNodesWithTag(LINK_TAG)[1].performTextInput(review[2])
        composeTestRule.onNodeWithTag(SAVE_TAG).performClick()
        composeTestRule.waitForIdle()

        // auto-navigates to question screen, so click back to review
        composeTestRule.onNodeWithTag(REVIEW).assertIsNotSelected().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(review[0]).assertExists()
        composeTestRule.onNodeWithText(REFERENCES).assertExists()
        // how to test ClickableText?
        //composeTestRule.onNodeWithText(review[1]).assertHasClickAction()
        /*
        // delete question
        composeTestRule.onNodeWithTag(DELETE_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(DELETE_R_TEXT).assertExists()
        composeTestRule.onNodeWithText(CONFIRM_TEXT).performClick()
        composeTestRule.waitForIdle()

        // should navigate to question, so click review to find empty
        composeTestRule.onNodeWithTag(QUESTION).assertIsSelected()
        composeTestRule.onNodeWithTag(REVIEW).assertIsNotSelected().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Question 1 / 1").assertExists()
        composeTestRule.onNodeWithText(REFERENCES).assertDoesNotExist() */
    }
}