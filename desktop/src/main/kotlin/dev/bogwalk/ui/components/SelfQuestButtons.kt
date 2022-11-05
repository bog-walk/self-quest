package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.bogwalk.models.QuizMode
import dev.bogwalk.ui.style.*

internal const val BACK_TAG = "back arrow"
internal const val BACK_DEFAULT_DESCRIPTION = "Previous question"
internal const val FORWARD_TAG = "forward arrow"
internal const val FORWARD_DESCRIPTION = "Next question"
internal const val SAVE_TAG = "save button"
internal const val ADD_LINK = "+ Add reference link"
internal const val TOGGLE_TAG = "mode toggle"
private const val BACK_ICON = "back.svg"
private const val FORWARD_ICON = "forward.svg"
private const val CONFIRM_TEXT = "Confirm"
private const val SAVE_TEXT = "Save"
private const val STUDY_TEXT = "Study"
private const val QUIZ_TEXT = "Quiz"
private const val QUIZ_ICON = "quiz.svg"
private const val QUIZ_DESCRIPTION = "Toggle quiz mode"
private val buttonStroke = 2.dp

@Composable
internal fun DialogButton(
    onConfirmRequest: () -> Unit
) {
    OutlinedButton(
        onClick = { onConfirmRequest() },
        modifier = Modifier.padding(smallDp),
        border = BorderStroke(buttonStroke, MaterialTheme.colors.primary)
    ) {
        Text(
            text = CONFIRM_TEXT,
            style = MaterialTheme.typography.button
        )
    }
}

@Composable
internal fun SaveFormButton(
    isEnabled: Boolean,
    onButtonClick: () -> Unit
) {
    Button(
        onClick = onButtonClick,
        modifier = Modifier.testTag(SAVE_TAG).padding(smallDp),
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onSurface
        )
    ) {
        Text(text = SAVE_TEXT, style = MaterialTheme.typography.button)
    }
}

@Composable
internal fun AddLinkButton(
    onAddLinkRequest: () -> Unit
) {
    Text(
        text = ADD_LINK,
        modifier = Modifier
            .padding(midDp)
            .clickable(
                enabled = true,
                onClickLabel = ADD_LINK.drop(2),
                role = Role.Button,
                onClick = { onAddLinkRequest() }
            ),
        color = MaterialTheme.colors.primary,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
internal fun ArrowButton(
    modifier: Modifier,
    description: String? = null,
    isEnabled: Boolean = true,
    isBackArrow: Boolean = true,
    onButtonClick: () -> Unit
) {
    IconButton(
        onClick = { onButtonClick() },
        modifier = modifier
            .testTag(if (isBackArrow) BACK_TAG else FORWARD_TAG)
            .padding(tinyDp),
        enabled = isEnabled
    ) {
        Icon(
            painter = painterResource(if (isBackArrow) BACK_ICON else FORWARD_ICON),
            contentDescription = description ?: if (isBackArrow) BACK_DEFAULT_DESCRIPTION else FORWARD_DESCRIPTION,
            modifier = Modifier.requiredSize(iconSize),
            tint = getIconTint(isEnabled)
        )
    }
}

@Composable
internal fun QuizModeSwitch(
    mode: QuizMode,
    onToggleMode: () -> Unit
) {
    Row(
        modifier = Modifier.padding(midDp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = STUDY_TEXT,
            style = MaterialTheme.typography.button
        )
        Icon(
            painter = painterResource(STUDY_ICON),
            contentDescription = STUDY_DESCRIPTION,
            modifier = Modifier.requiredSize(iconSize),
            tint = MaterialTheme.colors.onSurface
        )
        Switch(
            checked = mode == QuizMode.WAITING || mode == QuizMode.CHOSEN,
            onCheckedChange = { onToggleMode() },
            modifier = Modifier.testTag(TOGGLE_TAG),
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colors.primary,
                uncheckedThumbColor = MaterialTheme.colors.onSurface
            )
        )
        Icon(
            painter = painterResource(QUIZ_ICON),
            contentDescription = QUIZ_DESCRIPTION,
            modifier = Modifier.requiredSize(iconSize),
            tint = if (mode == QuizMode.WAITING || mode == QuizMode.CHOSEN) {
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.onSurface
            }
        )
        Text(
            text = QUIZ_TEXT,
            color = if (mode == QuizMode.WAITING || mode == QuizMode.CHOSEN) {
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.onSurface
            },
            style = MaterialTheme.typography.button
        )
    }
}

@Preview
@Composable
private fun DialogButtonPreview() {
    SelfQuestTheme {
        DialogButton {}
    }
}

@Preview
@Composable
private fun SaveFormButtonPreview() {
    SelfQuestTheme {
        Column {
            SaveFormButton(true) {}
            SaveFormButton(false) {}
        }
    }
}

@Preview
@Composable
private fun AddLinkButtonPreview() {
    SelfQuestTheme {
        AddLinkButton {}
    }
}

@Preview
@Composable
private fun ArrowButtonPreview() {
    SelfQuestTheme {
        Column {
            ArrowButton(Modifier) {}
            ArrowButton(Modifier, isEnabled = false) {}
            ArrowButton(Modifier, isBackArrow = false) {}
        }
    }
}

@Preview
@Composable
private fun QuizModeSwitchPreview() {
    SelfQuestTheme {
        Column {
            QuizModeSwitch(QuizMode.STUDYING) {}
            QuizModeSwitch(QuizMode.WAITING) {}
        }
    }
}