package dev.bogwalk.ui.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import dev.bogwalk.models.MainState
import dev.bogwalk.ui.style.BACK_TAG
import org.junit.Rule
import kotlin.test.Test

internal class LeftSideBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `LeftSideBar is empty if a Deck is not in overview`() {
        val deckTitle: MutableState<String?> = mutableStateOf("Title")
        composeTestRule.setContent {
            LeftSideBar(MainState.ALL_DECKS, deckTitle.value, {}) {}
        }

        composeTestRule
            .onNodeWithTag(BACK_TAG).assertExists()
        deckTitle.value?.let {
            composeTestRule
                .onNodeWithText(it).assertExists()
        }

        deckTitle.value = null
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(BACK_TAG).assertDoesNotExist()
        composeTestRule
            .onNodeWithText("Title").assertDoesNotExist()
    }
}