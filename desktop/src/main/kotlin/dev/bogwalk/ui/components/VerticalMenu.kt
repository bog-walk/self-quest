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

internal const val VERTICAL_TAG = "vertical menu"
internal const val ADD_TAG = "add icon"
internal const val ADD_DECK_DESCRIPTION = "Add collection"
internal const val ADD_QUESTION_DESCRIPTION = "Add question"
internal const val EDIT_TAG = "edit icon"
internal const val EDIT_DECK_DESCRIPTION = "Edit collection"
internal const val EDIT_QUESTION_DESCRIPTION = "Edit question"
internal const val EDIT_REVIEW_DESCRIPTION = "Edit review"
internal const val DELETE_TAG = "delete icon"
internal const val DELETE_DECK_DESCRIPTION = "Delete collection"
internal const val DELETE_QUESTION_DESCRIPTION = "Delete question"
internal const val DELETE_REVIEW_DESCRIPTION = "Delete review"
private const val ADD_ICON = "add.svg"
private const val EDIT_ICON = "edit.svg"
private const val DELETE_ICON = "delete.svg"

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
            .padding(smallDp)
            .zIndex(10f)
            .onPointerEvent(PointerEventType.Enter) { isInFocus = true }
            .onPointerEvent(PointerEventType.Exit) { isInFocus = false },
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colors.primary,
        elevation = tinyDp
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            MenuRow(
                menuIcon = ADD_ICON,
                description = when (screenState) {
                    MainState.ALL_DECKS -> ADD_DECK_DESCRIPTION
                    else -> ADD_QUESTION_DESCRIPTION
                },
                tag = ADD_TAG,
                isEnabled = screenState in MainState.values().take(3),
                focused = isInFocus,
                onRowClick = addRequested
            )
            MenuRow(
                menuIcon = EDIT_ICON,
                description = when (screenState) {
                    MainState.DECK_OVERVIEW -> EDIT_DECK_DESCRIPTION
                    MainState.IN_QUESTION -> EDIT_QUESTION_DESCRIPTION
                    else -> EDIT_REVIEW_DESCRIPTION
                },
                tag = EDIT_TAG,
                isEnabled = screenState in MainState.values().slice(1..3),
                focused = isInFocus,
                onRowClick = editRequested
            )
            MenuRow(
                menuIcon = DELETE_ICON,
                description = when (screenState) {
                    MainState.DECK_OVERVIEW -> DELETE_DECK_DESCRIPTION
                    MainState.IN_QUESTION -> DELETE_QUESTION_DESCRIPTION
                    else -> DELETE_REVIEW_DESCRIPTION
                },
                tag = DELETE_TAG,
                isEnabled = screenState in MainState.values().slice(1..3),
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
            .padding(smallDp)
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
                    modifier = Modifier.padding(horizontal = smallDp)
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
            MainState.values().forEach {
                VerticalMenu(Modifier, it, {}, {}, {})
            }
        }
    }
}