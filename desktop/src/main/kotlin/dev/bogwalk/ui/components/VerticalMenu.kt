package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import dev.bogwalk.ui.style.*
import dev.bogwalk.models.MainState

@Composable
fun VerticalMenu(
    modifier: Modifier,
    screenState: MainState,
    addRequested: () -> Unit,
    editRequested: () -> Unit,
    deleteRequested: () -> Unit
) {
    Card(
        modifier = modifier.testTag(VERTICAL_TAG)
            .padding(cardPadding)
            .zIndex(10f),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colors.primary,
        elevation = cardElevation
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // migrate to M3 to use IconButton with IconButtonColors
            IconButton(
                onClick = { addRequested() },
                modifier = Modifier.testTag(ADD_TAG).padding(cardElevation),
                enabled = screenState == MainState.ALL_DECKS || screenState == MainState.DECK_OVERVIEW
            ) {
                Icon(
                    painter = painterResource(ADD_ICON),
                    contentDescription = if (screenState == MainState.ALL_DECKS) ADD_DECK_DESCRIPTION else ADD_QUESTION_DESCRIPTION,
                    modifier = Modifier.requiredSize(iconSize)
                )
            }
            IconButton(
                onClick = { editRequested() },
                modifier = Modifier.testTag(EDIT_TAG).padding(cardElevation),
                enabled = screenState == MainState.DECK_OVERVIEW || screenState == MainState.IN_QUESTION
            ) {
                Icon(
                    painter = painterResource(EDIT_ICON),
                    contentDescription = if (screenState == MainState.DECK_OVERVIEW) EDIT_DECK_DESCRIPTION else EDIT_QUESTION_DESCRIPTION,
                    modifier = Modifier.requiredSize(iconSize)
                )
            }
            IconButton(
                onClick = { deleteRequested() },
                modifier = Modifier.testTag(DELETE_TAG).padding(cardElevation),
                enabled = screenState == MainState.DECK_OVERVIEW || screenState == MainState.IN_QUESTION
            ) {
                Icon(
                    painter = painterResource(DELETE_ICON),
                    contentDescription = if (screenState == MainState.DECK_OVERVIEW) DELETE_DECK_DESCRIPTION else DELETE_QUESTION_DESCRIPTION,
                    modifier = Modifier.requiredSize(iconSize)
                )
            }
        }
    }
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