package dev.bogwalk.ui.integration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import dev.bogwalk.ui.style.*

internal fun ComposeContentTestRule.assertAllDecksScreen(
    expectedDCount: Int = 0
) {
    onNodeWithTag(BACK_TAG).assertDoesNotExist()
    onAllNodesWithTag(DECK_TAG).assertCountEquals(expectedDCount)
    onNodeWithTag(VERTICAL_TAG).assertExists()
    onNodeWithTag(ADD_TAG).assertIsEnabled()
    onNodeWithTag(EDIT_TAG).assertIsNotEnabled()
    onNodeWithTag(DELETE_TAG).assertIsNotEnabled()
}

internal fun ComposeContentTestRule.assertDeckOverviewScreen(
    expectedTitle: String,
    expectedQCount: Int = 0
) {
    onNodeWithTag(BACK_TAG).assertExists()
    onNodeWithText(expectedTitle).assertExists()
    if (expectedQCount == 0) {
        onNodeWithTag(TOGGLE_TAG).assertDoesNotExist()
    } else {
        onNodeWithTag(TOGGLE_TAG).assertExists()
    }
    onAllNodesWithTag(QUEST_TAG).assertCountEquals(expectedQCount)
    onNodeWithTag(VERTICAL_TAG).assertExists()
    onNodeWithTag(ADD_TAG).assertIsEnabled()
    onNodeWithTag(EDIT_TAG).assertIsEnabled()
    onNodeWithTag(DELETE_TAG).assertIsEnabled()
}

internal fun ComposeContentTestRule.assertDeckForm(toAddNew: Boolean) {
    onNodeWithText("${if (toAddNew) ADD_HEADER else EDIT_HEADER} collection").assertExists()
    onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
    onNodeWithTag(BACK_TAG).assertExists()
}

internal fun ComposeContentTestRule.assertQuestionScreen(
    order: Pair<Int, Int>,
    canShowPrev: Boolean,
    canShowNext: Boolean,
    content: String,
    isStudying: Boolean = true,
    answerChosen: Boolean = false
) {
    onNodeWithText("Question ${order.first} / ${order.second}")
    onAllNodesWithTag(BACK_TAG).assertCountEquals(if (isStudying) 2 else 1)
        .filter(isEnabled()).assertCountEquals(1 + if (canShowPrev) 1 else 0)
    onNodeWithTag(FORWARD_TAG).assertExists()
        .assert(if (canShowNext) isEnabled() else isNotEnabled())
    onNodeWithText(content).assertExists()
    onAllNodesWithTag(ANSWER_TAG).assertCountEquals(4)
        .assertAll(if (answerChosen) isNotEnabled() else isEnabled())
    if (isStudying) {
        onNodeWithTag(VERTICAL_TAG).assertExists()
        onNodeWithTag(ADD_TAG).assertIsEnabled()
        onNodeWithTag(EDIT_TAG).assertIsEnabled()
        onNodeWithTag(DELETE_TAG).assertIsEnabled()
    }
}

internal fun ComposeContentTestRule.assertQuestionTabs(
    isInQuestion: Boolean,
    isStudying: Boolean
) {
    if (isStudying) {
        onNodeWithTag(QUESTION).assert(
            isEnabled() and (if (isInQuestion) isSelected() else isNotSelected())
        )
        onNodeWithTag(REVIEW).assert(
            isEnabled() and (if (isInQuestion) isNotSelected() else isSelected())
        )
    } else {
        onNodeWithTag(QUESTION).assert(isSelected() and isNotEnabled())
        onNodeWithTag(REVIEW).assert(isNotSelected() and isNotEnabled())
    }
}

internal fun ComposeContentTestRule.assertQuestionForm(toAddNew: Boolean) {
    onNodeWithText("${if (toAddNew) ADD_HEADER else EDIT_HEADER} question").assertExists()
    if (toAddNew) {
        onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
    } else {
        onNodeWithTag(SAVE_TAG).assertIsEnabled()
    }
    onNodeWithTag(BACK_TAG).assertExists()
    onNodeWithTag(CONTENT_TAG).assertExists()
    onAllNodesWithTag(RADIO_TAG).assertCountEquals(4)
}

internal fun ComposeContentTestRule.assertReviewScreen(
    order: Pair<Int, Int>,
    canShowPrev: Boolean,
    canShowNext: Boolean,
    content: String = "",
    hasReferences: Boolean = false
) {
    onNodeWithText("Question ${order.first} / ${order.second}")
    onAllNodesWithTag(BACK_TAG).assertCountEquals(2)
        .filter(isEnabled()).assertCountEquals(1 + if(canShowPrev) 1 else 0)
    onNodeWithTag(FORWARD_TAG).assertExists()
        .assert(if (canShowNext) isEnabled() else isNotEnabled())
    if (content.isNotEmpty()) {
        onNodeWithTag(CONTENT_TAG).assertExists().assert(hasTextExactly(content))
    } else {
        onNodeWithTag(CONTENT_TAG).assertDoesNotExist()
    }
    if (hasReferences) {
        onNodeWithText(REFERENCES).assertExists()
    } else {
        onNodeWithText(REFERENCES).assertDoesNotExist()
    }
    onNodeWithTag(VERTICAL_TAG).assertExists()
    onNodeWithTag(ADD_TAG).assertIsNotEnabled()
    onNodeWithTag(EDIT_TAG).assertIsEnabled()
    onNodeWithTag(DELETE_TAG).assertIsEnabled()
}

internal fun ComposeContentTestRule.assertReviewForm(
    canSave: Boolean,
    expectedRefCount: Int = 0
) {
    onNodeWithText("$EDIT_HEADER review").assertExists()
    onNodeWithTag(CONTENT_TAG).assertExists()
    onNodeWithText(ADD_LINK).assertIsEnabled()
    if (canSave) {
        onNodeWithTag(SAVE_TAG).assertIsEnabled()
    } else {
        onNodeWithTag(SAVE_TAG).assertIsNotEnabled()
    }
    onNodeWithTag(BACK_TAG).assertExists()
    onAllNodesWithTag(LINK_TAG).assertCountEquals(expectedRefCount)
}