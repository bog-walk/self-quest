package dev.bogwalk.ui.integration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.ui.SelfQuestApp
import dev.bogwalk.ui.style.*
import org.junit.Rule
import kotlin.test.Test

internal class BasicDecksTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // testing of Dialogs not fully supported?
    @Test
    fun `navigation between empty decks screen and decks form`() {
        composeTestRule.setContent {
            SelfQuestApp(TestAppState())
        }

        // app loads with no saved decks
        composeTestRule.onNodeWithTag(BACK_TAG).assertDoesNotExist()
        composeTestRule.onAllNodesWithTag(DECK_TAG).assertCountEquals(0)
        composeTestRule.onNodeWithTag(VERTICAL_TAG).assertExists()
        composeTestRule.onNodeWithTag(EDIT_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(DELETE_TAG).assertIsNotEnabled()

        // add a new deck
        composeTestRule.onNodeWithTag(ADD_TAG).performClick()
        composeTestRule.waitForIdle()

        // form screen opens
        composeTestRule.onNodeWithText("$ADD_HEADER collection").assertExists()
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(BACK_TAG).assertExists()
        /*
        // exit form using back button
        composeTestRule.onNodeWithTag(BACK_TAG).performClick()
        composeTestRule.waitForIdle()

        // confirm form exit
        composeTestRule.onNodeWithText(WARNING).assertExists()
        composeTestRule.onNodeWithText(CONFIRM_TEXT).performClick()
        composeTestRule.waitForIdle()

        // navigate to empty decks screen
        composeTestRule.onNodeWithTag(BACK_TAG).assertDoesNotExist()
        composeTestRule.onAllNodesWithTag(DECK_TAG).assertCountEquals(0)
        composeTestRule.onNodeWithTag(VERTICAL_TAG).assertExists()
        composeTestRule.onNodeWithTag(EDIT_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(DELETE_TAG).assertIsNotEnabled()*/
    }

    @Test
    fun `addition, editing, and deletion of a new Deck`() {
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

        // fill out form
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
        composeTestRule.onAllNodesWithTag(QUEST_TAG).assertCountEquals(0)
        composeTestRule.onNodeWithTag(VERTICAL_TAG).assertExists()
        composeTestRule.onNodeWithTag(ADD_TAG).assertIsEnabled()
        composeTestRule.onNodeWithTag(EDIT_TAG).assertIsEnabled()
        composeTestRule.onNodeWithTag(DELETE_TAG).assertIsEnabled()

        // edit Deck name
        composeTestRule.onNodeWithTag(EDIT_TAG).performClick()
        composeTestRule.waitForIdle()

        // fill out form
        composeTestRule.onNodeWithText("$EDIT_HEADER collection").assertExists()
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(BACK_TAG).assertExists()

        composeTestRule.onNodeWithTag(NAME_TAG).performTextClearance()
        composeTestRule.onNodeWithTag(NAME_TAG).performTextInput("$name 2")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsEnabled().performClick()
        composeTestRule.waitForIdle()

        // auto-navigates to deck overview
        composeTestRule.onNodeWithTag(BACK_TAG).assertExists()
        composeTestRule.onNodeWithText("$name 2").assertExists()
        composeTestRule.onAllNodesWithTag(QUEST_TAG).assertCountEquals(0)
        composeTestRule.onNodeWithTag(VERTICAL_TAG).assertExists()
        composeTestRule.onNodeWithTag(ADD_TAG).assertIsEnabled()
        composeTestRule.onNodeWithTag(EDIT_TAG).assertIsEnabled()
        composeTestRule.onNodeWithTag(DELETE_TAG).assertIsEnabled()

        // go to deck overview then navigate back
        composeTestRule.onNodeWithTag(BACK_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(BACK_TAG).assertDoesNotExist()
        composeTestRule.onAllNodesWithTag(DECK_TAG).assertCountEquals(1)
        /*
        composeTestRule.onNodeWithTag(DECK_TAG).performClick()
        composeTestRule.waitForIdle()

        // delete deck
        composeTestRule.onNodeWithTag(DELETE_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(DELETE_D_TEXT).assertExists()
        composeTestRule.onNodeWithText(CONFIRM_TEXT).performClick()
        composeTestRule.waitForIdle()

        // should navigate to empty deck screen
        composeTestRule.onNodeWithTag(BACK_TAG).assertDoesNotExist()
        composeTestRule.onAllNodesWithTag(DECK_TAG).assertCountEquals(0)
        composeTestRule.onNodeWithTag(VERTICAL_TAG).assertExists()
        composeTestRule.onNodeWithTag(ADD_TAG).assertIsEnabled()
        composeTestRule.onNodeWithTag(EDIT_TAG).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(DELETE_TAG).assertIsNotEnabled()*/
    }
}