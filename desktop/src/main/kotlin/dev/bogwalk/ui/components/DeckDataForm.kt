package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import dev.bogwalk.models.DataLength
import dev.bogwalk.models.Deck
import dev.bogwalk.ui.style.*

@Composable
fun DeckDataForm(
    deck: Deck?,
    onConfirmDeckData: (Deck) -> Unit
) {
    var name by remember { mutableStateOf(deck?.name ?: "") }
    val maxChar = DataLength.DeckName
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.widthIn(preferredWidth.first, preferredWidth.second).padding(cardPadding)
    ) {
        // Should this be extracted (along with QuestionScreen)?
        Text(
            text = "${if (deck == null) ADD_HEADER else EDIT_HEADER} collection",
            modifier = Modifier.align(Alignment.Start).padding(start = cardPadding),
            style = MaterialTheme.typography.h4
        )
        Spacer(Modifier.height(innerPadding))
        // Can this be extracted?
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it.take(maxChar)
                if (it.length > maxChar) focusManager.moveFocus(FocusDirection.Down)
            },
            modifier = Modifier.padding(cardPadding),
            label = { Text("Name") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.primary,
                focusedLabelColor = MaterialTheme.colors.primary
            )
        )
        Spacer(Modifier.height(innerPadding))
        // Can this be extracted (along with dialog buttons)?
        Button(
            onClick = { onConfirmDeckData(Deck(deck?.id ?: 1, "", deck?.size ?: 0)) },
            modifier = Modifier.padding(cardPadding),
            enabled = name.isNotEmpty() && name != deck?.name,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onSurface
            )
        ) {
            Text(text = SAVE_TEXT, style = MaterialTheme.typography.button)
        }
    }
}

@Preview
@Composable
private fun DeckDataFormAddNewPreview() {
    SelfQuestTheme {
        DeckDataForm( null) {}
    }
}

@Preview
@Composable
private fun DeckDataFormEditPreview() {
    SelfQuestTheme {
        DeckDataForm(Deck(1, "Equine", 5)) {}
    }
}