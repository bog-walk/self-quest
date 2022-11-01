package dev.bogwalk.ui.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.bogwalk.models.MainState
import dev.bogwalk.ui.style.BACK_ALL_DESCRIPTION
import dev.bogwalk.ui.style.BACK_FORM_DESCRIPTION
import dev.bogwalk.ui.style.BACK_TAG
import org.junit.Rule
import kotlin.test.Test

internal class LeftSideBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `LeftSideBar is empty if a Deck is not in overview`() {
        val deckTitle: MutableState<String?> = mutableStateOf("Title")
        val state = mutableStateOf(MainState.DECK_OVERVIEW)
        composeTestRule.setContent {
            LeftSideBar(state.value, deckTitle.value, {}) {}
        }

        composeTestRule
            .onNodeWithTag(BACK_TAG).assertExists()
        composeTestRule
            .onNodeWithTag(BACK_TAG).assert(hasContentDescriptionExactly(BACK_ALL_DESCRIPTION))
        deckTitle.value?.let {
            composeTestRule
                .onNodeWithText(it).assertExists()
        }

        deckTitle.value = null
        state.value = MainState.ALL_DECKS
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(BACK_TAG).assertDoesNotExist()
        composeTestRule
            .onNodeWithText("Title").assertDoesNotExist()
    }
    @Test
    fun `open forms always have LeftSideBar back button`() {
        val deckTitle: MutableState<String?> = mutableStateOf(null)
        val state = mutableStateOf(MainState.ALL_DECKS)
        composeTestRule.setContent {
            LeftSideBar(state.value, deckTitle.value, {}) {}
        }

        composeTestRule
            .onNodeWithTag(BACK_TAG).assertDoesNotExist()

        state.value = MainState.UPDATING_DECK
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(BACK_TAG).assertExists()
        composeTestRule
            .onNodeWithTag(BACK_TAG).assert(hasContentDescriptionExactly(BACK_FORM_DESCRIPTION))

        deckTitle.value = "Title"
        state.value = MainState.UPDATING_QUESTION
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(BACK_TAG).assertExists()
        composeTestRule
            .onNodeWithTag(BACK_TAG).assert(hasContentDescriptionExactly(BACK_FORM_DESCRIPTION))

        state.value = MainState.UPDATING_REVIEW
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(BACK_TAG).assertExists()
        composeTestRule
            .onNodeWithTag(BACK_TAG).assert(hasContentDescriptionExactly(BACK_FORM_DESCRIPTION))
    }
}