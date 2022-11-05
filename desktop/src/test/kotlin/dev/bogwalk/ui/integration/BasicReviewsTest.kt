package dev.bogwalk.ui.integration

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import dev.bogwalk.models.Review
import dev.bogwalk.ui.SelfQuestApp
import dev.bogwalk.ui.components.*
import org.junit.Rule
import kotlin.test.Test

internal class BasicReviewsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // testing of Dialogs not fully supported?
    @Test
    fun `addition, editing, and deletion of a new Review`() {
        val deckName = "New"
        val question = "Fake question"
        composeTestRule.setContent {
            val api = TestClient(
                listOf(Deck(1, deckName, 1)), mapOf(1 to listOf(Question(
                    1, question, listOf("A", "B", "C", "D"), "C", null
                )))
            )
            LaunchedEffect("initial load") {
                api.loadSavedDecks()
            }
            SelfQuestApp(api)
        }

        // app loads with 1 saved deck
        composeTestRule.assertAllDecksScreen(expectedDCount = 1)
        composeTestRule.onNodeWithTag(DECK_TAG).assert(hasText("1 question"))

        // open deck
        composeTestRule.onNodeWithTag(DECK_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertDeckOverviewScreen(deckName, expectedQCount = 1)

        // open question
        composeTestRule.onNodeWithTag(QUEST_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertQuestionScreen(
            1 to 1, canShowPrev = false, canShowNext = false, question
        )
        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = true)

        // select review tab to show empty screen
        composeTestRule.onNodeWithTag(REVIEW).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertQuestionTabs(isInQuestion = false, isStudying = true)
        composeTestRule.assertReviewScreen(1 to 1, canShowPrev = false, canShowNext = false)

        // add review (via editing)
        composeTestRule.onNodeWithTag(EDIT_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertReviewForm(canSave = false)

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
        composeTestRule.assertQuestionScreen(
            1 to 1, canShowPrev = false, canShowNext = false, question
        )
        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = true)

        composeTestRule.onNodeWithTag(REVIEW).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertReviewScreen(
            1 to 1, canShowPrev = false, canShowNext = false,
            content = review[0], hasReferences = true
        )

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
        composeTestRule.assertQuestionScreen(
            1 to 1, canShowPrev = false, canShowNext = false, question
        )
        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = true)

        composeTestRule.onNodeWithTag(REVIEW).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertQuestionTabs(isInQuestion = false, isStudying = true)
        composeTestRule.assertReviewScreen(1 to 1, canShowPrev = false, canShowNext = false)*/
    }

    @Test
    fun `navigate between question and review tabs`() {
        val deckName = "New"
        val question = "Fake question"
        val review = "Explanation"
        val questions = List(5) {
            Question(
                it + 1, question, listOf("A", "B", "C", "D"),
                "C", if (it % 2 == 0) null else Review(review, emptyList())
            )
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

        // select review tab to show empty screen
        composeTestRule.onNodeWithTag(REVIEW).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertQuestionTabs(isInQuestion = false, isStudying = true)
        composeTestRule.assertReviewScreen(1 to 5, canShowPrev = false, canShowNext = true)

        // move forward through reviews to fourth question (but still with review tab selected)
        repeat(3) {
            composeTestRule.onNodeWithTag(FORWARD_TAG).performClick()
            composeTestRule.waitForIdle()
        }

        composeTestRule.assertQuestionTabs(isInQuestion = false, isStudying = true)
        composeTestRule.assertReviewScreen(
            4 to 5, canShowPrev = true, canShowNext = true, content = review
        )

        // switch to question tab
        composeTestRule.onNodeWithTag(QUESTION).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = true)
        composeTestRule.assertQuestionScreen(
            4 to 5, canShowPrev = true, canShowNext = true, question
        )

        // move forward to last question then swap to review tab
        composeTestRule.onNodeWithTag(FORWARD_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = true)
        composeTestRule.assertQuestionScreen(
            5 to 5, canShowPrev = true, canShowNext = false, question
        )

        composeTestRule.onNodeWithTag(REVIEW).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertQuestionTabs(isInQuestion = false, isStudying = true)
        composeTestRule.assertReviewScreen(5 to 5, canShowPrev = true, canShowNext = false)

        // navigate back to deck overview from review screen
        composeTestRule.onAllNodesWithTag(BACK_TAG)[0].performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertDeckOverviewScreen(deckName, expectedQCount = 5)
    }
}