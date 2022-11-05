package dev.bogwalk.ui.integration

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import dev.bogwalk.ui.SelfQuestApp
import dev.bogwalk.ui.components.*
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

        // navigate to all decks and back
        composeTestRule.onAllNodesWithTag(BACK_TAG).filterToOne(isEnabled()).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertDeckOverviewScreen(deckName, expectedQCount = 1)

        composeTestRule.onNodeWithTag(BACK_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertAllDecksScreen(expectedDCount = 1)
        composeTestRule.onNodeWithTag(DECK_TAG).assert(hasText("1 question"))
        /*
        composeTestRule.onNodeWithTag(DECK_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(QUEST_TAG).performClick()
        composeTestRule.waitForIdle()

        // delete question
        composeTestRule.onNodeWithTag(DELETE_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(DELETE_Q_TEXT).assertExists()
        composeTestRule.onNodeWithText(CONFIRM_TEXT).performClick()
        composeTestRule.waitForIdle()

        // should navigate to empty deck overview
        composeTestRule.assertDeckOverviewScreen(deckName)

        // ensure all decks screen has deck card with correct text
        composeTestRule.onNodeWithTag(BACK_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertAllDecksScreen(expectedDCount = 1)
        composeTestRule.onNodeWithTag(DECK_TAG)
            .onChildren().assertAny(hasTextExactly("0 questions"))*/
    }

    @Test
    fun `addition of new question while viewing a question`() {
        val deckName = "New"
        val question = "Fake question"
        val questions = List(5) {
            Question(it + 1, question, listOf("A", "B", "C", "D"), "C", null)
        }
        composeTestRule.setContent {
            val api = TestClient(listOf(Deck(1, deckName, 5)), mapOf(1 to questions))
            LaunchedEffect("initial load") {
                api.loadSavedDecks()
            }
            SelfQuestApp(api)
        }

        // app loads with 1 saved deck
        composeTestRule.assertAllDecksScreen(expectedDCount = 1)
        composeTestRule.onNodeWithTag(DECK_TAG).assert(hasText("5 questions"))

        // open deck
        composeTestRule.onNodeWithTag(DECK_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertDeckOverviewScreen(deckName, expectedQCount = 5)

        // open first question
        composeTestRule.onAllNodesWithTag(QUEST_TAG)[0].performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertQuestionScreen(
            1 to 5, canShowPrev = false, canShowNext = true, question
        )
        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = true)

        // add new Question
        composeTestRule.onNodeWithTag(ADD_TAG).performClick()
        composeTestRule.waitForIdle()

        // fill out form
        composeTestRule.assertQuestionForm(toAddNew = true)

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

        // auto-navigates to new question screen
        composeTestRule.assertQuestionScreen(
            1 to 6, canShowPrev = false, canShowNext = true, question
        )
        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = true)

        // move forward to middle of collection
        repeat(3) {
            composeTestRule.onNodeWithTag(FORWARD_TAG).performClick()
            composeTestRule.waitForIdle()
        }

        composeTestRule.assertQuestionScreen(
            4 to 6, canShowPrev = true, canShowNext = true, question
        )
        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = true)

        // add new Question
        composeTestRule.onNodeWithTag(ADD_TAG).performClick()
        composeTestRule.waitForIdle()

        // fill out form
        composeTestRule.assertQuestionForm(toAddNew = true)

        composeTestRule.onNodeWithTag(CONTENT_TAG).performTextInput(question)
        composeTestRule.onNodeWithTag("Answer 1").performTextInput("A")
        composeTestRule.onNodeWithTag("Answer 2").performTextInput("B")
        composeTestRule.onNodeWithTag("Answer 3").performTextInput("C")
        composeTestRule.onNodeWithTag("Answer 4").performTextInput("D")
        composeTestRule.onAllNodesWithTag(RADIO_TAG)[0].performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsEnabled().performClick()
        composeTestRule.waitForIdle()

        // auto-navigates to new question screen
        composeTestRule.assertQuestionScreen(
            1 to 7, canShowPrev = false, canShowNext = true, question
        )
        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = true)

        // navigate to all decks
        composeTestRule.onAllNodesWithTag(BACK_TAG).filterToOne(isEnabled()).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertDeckOverviewScreen(deckName, expectedQCount = 7)

        composeTestRule.onNodeWithTag(BACK_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertAllDecksScreen(expectedDCount = 1)
        composeTestRule.onNodeWithTag(DECK_TAG).assert(hasText("7 questions"))
    }

    @Test
    fun `editing existing Question retains its place in app memory`() {
        val deckName = "New"
        val question = "Fake question"
        val questions = List(5) {
            Question(it + 1, question, listOf("A", "B", "C", "D"), "C", null)
        }
        composeTestRule.setContent {
            val api = TestClient(listOf(Deck(1, deckName, 5)), mapOf(1 to questions))
            LaunchedEffect("initial load") {
                api.loadSavedDecks()
            }
            SelfQuestApp(api)
        }

        // app loads with 1 saved deck
        composeTestRule.assertAllDecksScreen(expectedDCount = 1)

        // open deck
        composeTestRule.onNodeWithTag(DECK_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertDeckOverviewScreen(deckName, expectedQCount = 5)

        // open third question
        composeTestRule.onAllNodesWithTag(QUEST_TAG)[2].performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertQuestionScreen(
            3 to 5, canShowPrev = true, canShowNext = true, question
        )
        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = true)

        // edit Question
        composeTestRule.onNodeWithTag(EDIT_TAG).performClick()
        composeTestRule.waitForIdle()

        // fill out form
        composeTestRule.assertQuestionForm(toAddNew = false)

        composeTestRule.onNodeWithTag(CONTENT_TAG).performTextInput(" updated")
        composeTestRule.onNodeWithTag(SAVE_TAG).assertIsEnabled().performClick()
        composeTestRule.waitForIdle()

        // auto-navigates to edited question screen
        composeTestRule.assertQuestionScreen(
            3 to 5, canShowPrev = true, canShowNext = true, "$question updated"
        )
        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = true)

        // move back to deck overview
        composeTestRule.onAllNodesWithTag(BACK_TAG)[0].performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertDeckOverviewScreen(deckName, expectedQCount = 5)
        composeTestRule.onAllNodesWithTag(QUEST_TAG)[2].assert(hasText("$question updated"))
    }
}