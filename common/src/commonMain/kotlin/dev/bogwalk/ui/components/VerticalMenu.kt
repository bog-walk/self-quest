package dev.bogwalk.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.zIndex
import dev.bogwalk.common.generated.resources.Res
import dev.bogwalk.common.generated.resources.add
import dev.bogwalk.common.generated.resources.delete
import dev.bogwalk.common.generated.resources.edit
import dev.bogwalk.models.MainState
import dev.bogwalk.ui.style.*
import org.jetbrains.compose.resources.painterResource

const val VERTICAL_TAG = "vertical menu"
const val ADD_TAG = "add icon"
const val ADD_DECK_DESCRIPTION = "Add collection"
const val ADD_QUESTION_DESCRIPTION = "Add question"
const val EDIT_TAG = "edit icon"
const val EDIT_DECK_DESCRIPTION = "Edit collection"
const val EDIT_QUESTION_DESCRIPTION = "Edit question"
const val EDIT_REVIEW_DESCRIPTION = "Edit review"
const val DELETE_TAG = "delete icon"
const val DELETE_DECK_DESCRIPTION = "Delete collection"
const val DELETE_QUESTION_DESCRIPTION = "Delete question"
const val DELETE_REVIEW_DESCRIPTION = "Delete review"

private enum class MenuIcon { ADD, EDIT, DELETE }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VerticalMenu(
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = tinyDp
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            MenuRow(
                menuIcon = MenuIcon.ADD,
                description = when (screenState) {
                    MainState.ALL_DECKS -> ADD_DECK_DESCRIPTION
                    else -> ADD_QUESTION_DESCRIPTION
                },
                tag = ADD_TAG,
                isEnabled = screenState in MainState.entries.toTypedArray().take(3),
                focused = isInFocus,
                onRowClick = addRequested
            )
            MenuRow(
                menuIcon = MenuIcon.EDIT,
                description = when (screenState) {
                    MainState.DECK_OVERVIEW -> EDIT_DECK_DESCRIPTION
                    MainState.IN_QUESTION -> EDIT_QUESTION_DESCRIPTION
                    else -> EDIT_REVIEW_DESCRIPTION
                },
                tag = EDIT_TAG,
                isEnabled = screenState in MainState.entries.toTypedArray().slice(1..3),
                focused = isInFocus,
                onRowClick = editRequested
            )
            MenuRow(
                menuIcon = MenuIcon.DELETE,
                description = when (screenState) {
                    MainState.DECK_OVERVIEW -> DELETE_DECK_DESCRIPTION
                    MainState.IN_QUESTION -> DELETE_QUESTION_DESCRIPTION
                    else -> DELETE_REVIEW_DESCRIPTION
                },
                tag = DELETE_TAG,
                isEnabled = screenState in MainState.entries.toTypedArray().slice(1..3),
                focused = isInFocus,
                onRowClick = deleteRequested
            )
        }
    }
}

@Composable
private fun MenuRow(
    menuIcon: MenuIcon,
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
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = smallDp)
                )
            }
        }
        Icon(
            painter = painterResource(when (menuIcon) {
                MenuIcon.ADD -> Res.drawable.add
                MenuIcon.EDIT -> Res.drawable.edit
                MenuIcon.DELETE -> Res.drawable.delete
            }),
            contentDescription = description,
            modifier = Modifier.requiredSize(iconSize),
            tint = getIconTint(isEnabled)
        )
    }
}

@Composable
internal fun getIconTint(isEnabled: Boolean) = if (isEnabled) {
    MaterialTheme.colorScheme.onSurface
} else {
    MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled)
}

@Composable
@Preview
private fun VerticalMenuPreview() {
    SelfQuestTheme {
        Row {
            MainState.entries.forEach {
                VerticalMenu(Modifier, it, {}, {}, {})
            }
        }
    }
}