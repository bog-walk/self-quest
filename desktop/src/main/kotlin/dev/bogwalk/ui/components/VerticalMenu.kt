package dev.bogwalk.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.zIndex
import dev.bogwalk.ui.style.*
import dev.bogwalk.models.MainState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun VerticalMenu(
    modifier: Modifier,
    screenState: MainState,
    addRequested: () -> Unit,
    editRequested: () -> Unit,
    deleteRequested: () -> Unit
) {
    var isInFocus by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.testTag(VERTICAL_TAG)
            .padding(cardPadding)
            .zIndex(10f)
            .onPointerEvent(PointerEventType.Enter) { isInFocus = true }
            .onPointerEvent(PointerEventType.Exit) { isInFocus = false },
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colors.primary,
        elevation = cardElevation
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            MenuRow(
                menuIcon = ADD_ICON,
                description = if (screenState == MainState.ALL_DECKS) ADD_DECK_DESCRIPTION else ADD_QUESTION_DESCRIPTION,
                tag = ADD_TAG,
                isEnabled = screenState == MainState.ALL_DECKS || screenState == MainState.DECK_OVERVIEW,
                focused = isInFocus,
                onRowClick = addRequested
            )
            MenuRow(
                menuIcon = EDIT_ICON,
                description = if (screenState == MainState.DECK_OVERVIEW) EDIT_DECK_DESCRIPTION else EDIT_QUESTION_DESCRIPTION,
                tag = EDIT_TAG,
                isEnabled = screenState == MainState.DECK_OVERVIEW || screenState == MainState.IN_QUESTION,
                focused = isInFocus,
                onRowClick = editRequested
            )
            MenuRow(
                menuIcon = DELETE_ICON,
                description = if (screenState == MainState.DECK_OVERVIEW) DELETE_DECK_DESCRIPTION else DELETE_QUESTION_DESCRIPTION,
                tag = DELETE_TAG,
                isEnabled = screenState == MainState.DECK_OVERVIEW || screenState == MainState.IN_QUESTION,
                focused = isInFocus,
                onRowClick = deleteRequested
            )
        }
    }
}

@Composable
private fun MenuRow(
    menuIcon: String,
    description: String,
    tag: String,
    isEnabled: Boolean,
    focused: Boolean,
    onRowClick: () -> Unit
) {
    Row(
        modifier = Modifier.testTag(tag)
            .padding(cardPadding)
            .clickable(
                enabled = isEnabled,
                onClickLabel = description,
                role = Role.Button,
                onClick = { onRowClick() }
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEnabled) {
            AnimatedVisibility(
                visible = focused,
                enter = expandHorizontally(animationSpec = tween(animationDuration))
                        + fadeIn(animationSpec = tween(animationDuration)),
                exit = shrinkHorizontally(animationSpec = tween(animationDuration))
                        + fadeOut(animationSpec = tween(animationDuration))
            ) {
                Text(
                    text = description,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(horizontal = cardPadding)
                )
            }
        }
        Icon(
            painter = painterResource(menuIcon),
            contentDescription = description,
            modifier = Modifier.requiredSize(iconSize),
            tint = getIconTint(isEnabled)
        )
    }
}

@Composable
internal fun getIconTint(isEnabled: Boolean) = if (isEnabled) {
    MaterialTheme.colors.onSurface
} else {
    MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
}

@Composable
@Preview
private fun VerticalMenuPreview() {
    SelfQuestTheme {
        Row {
            VerticalMenu(Modifier, MainState.ALL_DECKS, {}, {}) {}
            VerticalMenu(Modifier, MainState.DECK_OVERVIEW, {}, {}) {}
            VerticalMenu(Modifier, MainState.IN_QUESTION, {}, {}) {}
            VerticalMenu(Modifier, MainState.UPDATING_DECK, {}, {}) {}
        }
    }
}