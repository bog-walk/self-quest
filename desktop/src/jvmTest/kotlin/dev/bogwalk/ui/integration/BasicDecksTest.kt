package dev.bogwalk.ui.integration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.ui.SelfQuestApp
import dev.bogwalk.ui.components.*
import org.junit.Rule
import kotlin.test.Test

internal class BasicDecksTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // testing of Dialogs not fully supported?
    @Test
    fun `navigation between empty decks screen and decks form`() {
        composeTestRule.setContent {
            SelfQuestApp(TestClient())
        }

        // app loads with no saved decks
        composeTestRule.assertAllDecksScreen()

        // add a new deck
        composeTestRule.onNodeWithTag(ADD_TAG).performClick()
        composeTestRule.waitForIdle()

        // fill out form
        composeTestRule.assertDeckForm(toAddNew = true)
        /*
        // exit form using back button
        composeTestRule.onNodeWithTag(BACK_TAG).performClick()
        composeTestRule.waitForIdle()

        // confirm form exit
        composeTestRule.onNodeWithText(WARNING).assertExists()
        composeTestRule.onNodeWithText(CONFIRM_TEXT).performClick()
        composeTestRule.waitForIdle()

        // should navigate to empty decks screen
        composeTestRule.assertAllDecksScreen()*/
    }

    @Test
    fun `addition, editing, and deletion of a new Deck`() {
        composeTestRule.setContent {
            SelfQuestApp(TestClient())
        }

        // app loads with no saved decks
        composeTestRule.assertAllDecksScreen()

        // add a new deck
        composeTestRule.onNodeWithTag(ADD_TAG).performClick()
        composeTestRule.waitForIdle()

        // fill out form
        composeTestRule.assertDeckForm(toAddNew = true)

        val name = "Test Collection"
        composeTestRule.onNodeWithTag(NAME_TAG).performTextInput(name)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsEnabled().performClick()
        composeTestRule.waitForIdle()

        // auto-navigates to empty deck overview
        composeTestRule.assertDeckOverviewScreen(name)

        // edit Deck name
        composeTestRule.onNodeWithTag(EDIT_TAG).performClick()
        composeTestRule.waitForIdle()

        // fill out form
        composeTestRule.assertDeckForm(toAddNew = false)

        composeTestRule.onNodeWithTag(NAME_TAG).performTextClearance()
        composeTestRule.onNodeWithTag(NAME_TAG).performTextInput("$name 2")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsEnabled().performClick()
        composeTestRule.waitForIdle()

        // auto-navigates to updated but empty deck overview
        composeTestRule.assertDeckOverviewScreen("$name 2")

        // go to deck screen then navigate back
        composeTestRule.onNodeWithTag(BACK_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertAllDecksScreen(expectedDCount = 1)
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
        composeTestRule.assertAllDecksScreen()*/
    }
}