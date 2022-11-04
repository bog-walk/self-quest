package dev.bogwalk.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.ui.style.*
import dev.bogwalk.models.MainState
import org.junit.Rule
import kotlin.test.Test

internal class VerticalMenuTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `VerticalMenu icons all disabled if any forms are open`() {
        val mode = mutableStateOf(MainState.UPDATING_DECK)
        composeTestRule.setContent {
            VerticalMenu(Modifier, mode.value, {}, {}) {}
        }

        composeTestRule
            .onNodeWithTag(ADD_TAG).assertIsNotEnabled()
        composeTestRule
            .onNodeWithTag(EDIT_TAG).assertIsNotEnabled()
        composeTestRule
            .onNodeWithTag(DELETE_TAG).assertIsNotEnabled()

        mode.value = MainState.UPDATING_QUESTION
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(ADD_TAG).assertIsNotEnabled()
        composeTestRule
            .onNodeWithTag(EDIT_TAG).assertIsNotEnabled()
        composeTestRule
            .onNodeWithTag(DELETE_TAG).assertIsNotEnabled()

        mode.value = MainState.UPDATING_REVIEW
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(ADD_TAG).assertIsNotEnabled()
        composeTestRule
            .onNodeWithTag(EDIT_TAG).assertIsNotEnabled()
        composeTestRule
            .onNodeWithTag(DELETE_TAG).assertIsNotEnabled()
    }

    @Test
    fun `only add icon enabled on all decks screen`() {
        composeTestRule.setContent {
            VerticalMenu(Modifier, MainState.ALL_DECKS, {}, {}) {}
        }

        composeTestRule
            .onNodeWithTag(ADD_TAG).assertIsEnabled()
            .assertContentDescriptionEquals(ADD_DECK_DESCRIPTION)
        composeTestRule
            .onNodeWithTag(EDIT_TAG).assertIsNotEnabled()
        composeTestRule
            .onNodeWithTag(DELETE_TAG).assertIsNotEnabled()
    }

    @Test
    fun `VerticalMenu icons all enabled on deck overview and in question screen`() {
        val mode = mutableStateOf(MainState.DECK_OVERVIEW)
        composeTestRule.setContent {
            VerticalMenu(Modifier, mode.value, {}, {}) {}
        }

        composeTestRule
            .onNodeWithTag(ADD_TAG).assertIsEnabled()
            .assertContentDescriptionEquals(ADD_QUESTION_DESCRIPTION)
        composeTestRule
            .onNodeWithTag(EDIT_TAG).assertIsEnabled()
            .assertContentDescriptionEquals(EDIT_DECK_DESCRIPTION)
        composeTestRule
            .onNodeWithTag(DELETE_TAG).assertIsEnabled()
            .assertContentDescriptionEquals(DELETE_DECK_DESCRIPTION)

        mode.value = MainState.IN_QUESTION
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(ADD_TAG).assertIsEnabled()
            .assertContentDescriptionEquals(ADD_QUESTION_DESCRIPTION)
        composeTestRule
            .onNodeWithTag(EDIT_TAG).assertIsEnabled()
            .assertContentDescriptionEquals(EDIT_QUESTION_DESCRIPTION)
        composeTestRule
            .onNodeWithTag(DELETE_TAG).assertIsEnabled()
            .assertContentDescriptionEquals(DELETE_QUESTION_DESCRIPTION)
    }

    @Test
    fun `add icon is disabled on review detail screen`() {
        composeTestRule.setContent {
            VerticalMenu(Modifier, MainState.IN_REVIEW, {}, {}) {}
        }

        composeTestRule
            .onNodeWithTag(ADD_TAG).assertIsNotEnabled()
        composeTestRule
            .onNodeWithTag(EDIT_TAG).assertIsEnabled()
            .assertContentDescriptionEquals(EDIT_REVIEW_DESCRIPTION)
        composeTestRule
            .onNodeWithTag(DELETE_TAG).assertIsEnabled()
            .assertContentDescriptionEquals(DELETE_REVIEW_DESCRIPTION)
    }
}