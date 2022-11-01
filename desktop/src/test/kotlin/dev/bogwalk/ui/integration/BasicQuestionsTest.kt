package dev.bogwalk.ui.integration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.ui.SelfQuestApp
import dev.bogwalk.ui.style.*
import org.junit.Rule
import kotlin.test.Test

internal class BasicQuestionsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // testing of Dialogs not fully supported?
    @Test
    fun `addition, editing, and deletion of a new Question`() {
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

        // fill out form
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

        // edit question
        composeTestRule.onNodeWithTag(EDIT_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsEnabled()
        composeTestRule.onNodeWithTag(CONTENT_TAG).performTextClearance()
        composeTestRule.onNodeWithTag(CONTENT_TAG).performTextInput("$question updated")
        composeTestRule.onNodeWithTag(SAVE_TAG).performClick()
        composeTestRule.waitForIdle()

        // auto-navigates to question screen
        composeTestRule
            .onAllNodesWithTag(BACK_TAG).assertCountEquals(2)
            .filter(isEnabled()).assertCountEquals(1)
        composeTestRule.onNodeWithTag(FORWARD_TAG).assertExists().assertIsNotEnabled()
        composeTestRule.onNodeWithText("$question updated").assertExists()
        composeTestRule.onAllNodesWithTag(ANSWER_TAG).assertCountEquals(4)
        composeTestRule.onNodeWithTag(VERTICAL_TAG).assertExists()
        composeTestRule.onNodeWithTag(ADD_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(EDIT_TAG).assertIsEnabled()
        composeTestRule.onNodeWithTag(DELETE_TAG).assertIsEnabled()

        // navigate to deck overview and back
        composeTestRule.onAllNodesWithTag(BACK_TAG).filterToOne(isEnabled()).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithTag(QUEST_TAG).assertCountEquals(1)
        /*
        composeTestRule.onNodeWithTag(QUEST_TAG).performClick()
        composeTestRule.waitForIdle()

        // delete question
        composeTestRule.onNodeWithTag(DELETE_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(DELETE_Q_TEXT).assertExists()
        composeTestRule.onNodeWithText(CONFIRM_TEXT).performClick()
        composeTestRule.waitForIdle()

        // should navigate to deck overview
        composeTestRule.onNodeWithText(name).assertExists()
        composeTestRule.onAllNodesWithTag(QUEST_TAG).assertCountEquals(0)
        composeTestRule.onNodeWithTag(VERTICAL_TAG).assertExists()*/
    }
}