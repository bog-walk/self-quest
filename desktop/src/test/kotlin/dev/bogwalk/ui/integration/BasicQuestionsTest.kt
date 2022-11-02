package dev.bogwalk.ui.integration

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.models.Deck
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
        val deckName = "New"
        composeTestRule.setContent {
            val api = TestClient(listOf(Deck(1, deckName, 0)))
            LaunchedEffect("initial load") {
                api.loadSavedDecks()
            }
            SelfQuestApp(api)
        }

        // app loads with 1 saved deck
        composeTestRule.assertAllDecksScreen(expectedDCount = 1)

        // open empty deck
        composeTestRule.onNodeWithTag(DECK_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertDeckOverviewScreen(deckName)

        // add Question
        composeTestRule.onNodeWithTag(ADD_TAG).performClick()
        composeTestRule.waitForIdle()

        // fill out form
        composeTestRule.assertQuestionForm(toAddNew = true)

        val question = "Fake question"
        composeTestRule.onNodeWithTag(CONTENT_TAG).performTextInput(question)
        composeTestRule.onNodeWithTag("Answer 1").performTextInput("A")
        composeTestRule.onNodeWithTag("Answer 2").performTextInput("B")
        composeTestRule.onNodeWithTag("Answer 3").performTextInput("C")
        composeTestRule.onNodeWithTag("Answer 4").performTextInput("D")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsNotEnabled()

        composeTestRule.onAllNodesWithTag(RADIO_TAG)[0].performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsEnabled().performClick()
        composeTestRule.waitForIdle()

        // auto-navigates to question screen
        composeTestRule.assertQuestionScreen(
            1 to 1, canShowPrev = false, canShowNext = false, question
        )
        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = true)

        // edit question
        composeTestRule.onNodeWithTag(EDIT_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertQuestionForm(toAddNew = false)

        // fill out form
        composeTestRule.onNodeWithTag(CONTENT_TAG).performTextClearance()
        composeTestRule.onNodeWithTag(CONTENT_TAG).performTextInput("$question updated")
        composeTestRule.onNodeWithTag(SAVE_TAG).performClick()
        composeTestRule.waitForIdle()

        // auto-navigates to question screen
        composeTestRule.assertQuestionScreen(
            1 to 1, canShowPrev = false, canShowNext = false, "$question updated"
        )
        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = true)

        // navigate to deck overview and back
        composeTestRule.onAllNodesWithTag(BACK_TAG).filterToOne(isEnabled()).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertDeckOverviewScreen(deckName, expectedQCount = 1)
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
        composeTestRule.assertDeckOverviewScreen(deckName)*/
    }
}