package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import dev.bogwalk.ui.style.*

@Composable
fun VerticalMenu(
    modifier: Modifier,
    inDeckView: Boolean,
    addRequested: () -> Unit,
    editRequested: (String) -> Unit,
    deleteRequested: (String) -> Unit
) {
    Card(
        modifier = modifier.padding(cardPadding),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colors.background,
        elevation = cardElevation
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = { addRequested() },
                modifier = Modifier.padding(cardElevation)
            ) {
                Icon(
                    painter = painterResource(ADD_ICON),
                    contentDescription = if (inDeckView) ADD_DECK_DESCRIPTION else ADD_QUESTION_DESCRIPTION,
                    modifier = Modifier.requiredSize(iconSize)
                )
            }
            IconButton(
                onClick = { editRequested("id") },
                modifier = Modifier.padding(cardElevation)
            ) {
                Icon(
                    painter = painterResource(EDIT_ICON),
                    contentDescription = if (inDeckView) EDIT_DECK_DESCRIPTION else EDIT_QUESTION_DESCRIPTION,
                    modifier = Modifier.requiredSize(iconSize)
                )
            }
            IconButton(
                onClick = { deleteRequested("id") },
                modifier = Modifier.padding(cardElevation)
            ) {
                Icon(
                    painter = painterResource(DELETE_ICON),
                    contentDescription = if (inDeckView) DELETE_DECK_DESCRIPTION else DELETE_QUESTION_DESCRIPTION,
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
        VerticalMenu(Modifier, true, {}, {}) {}
    }
}