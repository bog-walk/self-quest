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
import dev.bogwalk.models.QuizMode
import dev.bogwalk.ui.style.*

@Composable
internal fun DialogButton(
    onConfirmRequest: () -> Unit
) {
    OutlinedButton(
        onClick = { onConfirmRequest() },
        modifier = Modifier.padding(cardPadding),
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
        modifier = Modifier.testTag(SAVE_TAG).padding(cardPadding),
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
            .padding(innerPadding)
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
    isEnabled: Boolean = true,
    isBackArrow: Boolean = true,
    onButtonClick: () -> Unit
) {
    IconButton(
        onClick = { onButtonClick() },
        modifier = modifier
            .testTag(if (isBackArrow) BACK_TAG else FORWARD_TAG)
            .padding(cardElevation),
        enabled = isEnabled
    ) {
        Icon(
            painter = painterResource(if (isBackArrow) BACK_ICON else FORWARD_ICON),
            contentDescription = if (isBackArrow) BACK_DESCRIPTION else FORWARD_DESCRIPTION,
            modifier = Modifier.requiredSize(iconSize),
            tint = getIconTint(isEnabled)
        )
    }
}

@Composable
fun QuizModeSwitch(
    mode: QuizMode,
    onToggleMode: () -> Unit
) {
    Row(
        modifier = Modifier.padding(innerPadding).fillMaxWidth(),
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