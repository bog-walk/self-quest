package dev.bogwalk.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.ui.style.*
import dev.bogwalk.models.MainState
import org.junit.Rule
import kotlin.test.Test

// cannot test fully due to NotImplementedError with performMouseInput() (upgrade compose?)
internal class VerticalMenuTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `VerticalMenu icons all disabled if deck or question is being updated`() {
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
    }

    @Test
    fun `only add icon enabled on all decks screen`() {
        composeTestRule.setContent {
            VerticalMenu(Modifier, MainState.ALL_DECKS, {}, {}) {}
        }

        composeTestRule
            .onNodeWithTag(ADD_TAG).assertIsEnabled()
        composeTestRule
            .onNodeWithTag(EDIT_TAG).assertIsNotEnabled()
        composeTestRule
            .onNodeWithTag(DELETE_TAG).assertIsNotEnabled()
    }

    @Test
    fun `VerticalMenu icons all enabled on deck overview screen`() {
        composeTestRule.setContent {
            VerticalMenu(Modifier, MainState.DECK_OVERVIEW, {}, {}) {}
        }

        composeTestRule
            .onNodeWithTag(ADD_TAG).assertIsEnabled()
        composeTestRule
            .onNodeWithTag(EDIT_TAG).assertIsEnabled()
        composeTestRule
            .onNodeWithTag(DELETE_TAG).assertIsEnabled()
    }

    @Test
    fun `add icon in disabled on question detail screen`() {
        composeTestRule.setContent {
            VerticalMenu(Modifier, MainState.IN_QUESTION, {}, {}) {}
        }

        composeTestRule
            .onNodeWithTag(ADD_TAG).assertIsNotEnabled()
        composeTestRule
            .onNodeWithTag(EDIT_TAG).assertIsEnabled()
        composeTestRule
            .onNodeWithTag(DELETE_TAG).assertIsEnabled()
    }
}