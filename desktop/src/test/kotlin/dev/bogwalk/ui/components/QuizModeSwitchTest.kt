package dev.bogwalk.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import dev.bogwalk.models.QuizMode
import dev.bogwalk.ui.style.TOGGLE_TAG
import org.junit.Rule
import kotlin.test.Test

internal class QuizModeSwitchTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `QuizModeSwitch checked correctly based on mode`() {
        val mode = mutableStateOf(QuizMode.STUDYING)
        composeTestRule.setContent {
            QuizModeSwitch(mode.value) {}
        }

        composeTestRule
            .onNodeWithTag(TOGGLE_TAG).assertIsOff()

        mode.value = QuizMode.WAITING
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(TOGGLE_TAG).assertIsOn()
    }
}