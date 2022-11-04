package dev.bogwalk.ui.integration

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import dev.bogwalk.models.Review
import dev.bogwalk.ui.SelfQuestApp
import dev.bogwalk.ui.style.*
import org.junit.Rule
import kotlin.test.Test

internal class QuizToggleTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `toggle between study and quiz mode`() {
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
            val api = TestClient(listOf(Deck(1, deckName, 0)), mapOf(1 to questions))
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

        // toggle to quiz mode
        composeTestRule.onNodeWithTag(TOGGLE_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(TOGGLE_TAG).assertIsOn()
        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = false)
        composeTestRule.assertQuestionScreen(
            3 to 5, canShowPrev = false, canShowNext = false, question, isStudying = false
        )

        // pick an answer, then move forward
        composeTestRule.onAllNodesWithTag(ANSWER_TAG)[0].performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = false)
        composeTestRule.assertQuestionScreen(
            3 to 5, canShowPrev = false, canShowNext = true, question, isStudying = false, answerChosen = true
        )

        composeTestRule.onNodeWithTag(FORWARD_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = false)
        composeTestRule.assertQuestionScreen(
            4 to 5, canShowPrev = false, canShowNext = false, question, isStudying = false
        )

        // toggle back to study mode
        composeTestRule.onNodeWithTag(TOGGLE_TAG).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(TOGGLE_TAG).assertIsOff()
        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = true)
        composeTestRule.assertQuestionScreen(
            4 to 5, canShowPrev = true, canShowNext = true, question
        )

        // navigate back to deck overview and toggle to quiz mode
        composeTestRule.onAllNodesWithTag(BACK_TAG)[0].performClick()
        composeTestRule.waitForIdle()

        composeTestRule.assertDeckOverviewScreen(deckName, expectedQCount = 5)
        composeTestRule.onNodeWithTag(TOGGLE_TAG).performClick()
        composeTestRule.waitForIdle()

        // should navigate to first question
        composeTestRule.onNodeWithTag(TOGGLE_TAG).assertIsOn()
        composeTestRule.assertQuestionTabs(isInQuestion = true, isStudying = false)
        composeTestRule.assertQuestionScreen(
            1 to 5, canShowPrev = false, canShowNext = false, question, isStudying = false
        )
    }
}