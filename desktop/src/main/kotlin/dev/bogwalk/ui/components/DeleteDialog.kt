package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.WindowPosition
import dev.bogwalk.ui.style.*
import dev.bogwalk.models.MainState

@Composable
fun DeleteDialog(
    screenState: MainState,
    onConfirmRequest: () -> Unit,
    onCloseRequest: () -> Unit
) {
    Dialog(
        onCloseRequest = { onCloseRequest() },
        state = DialogState(WindowPosition(Alignment.Center), dialogSize),
        title = "",
        resizable = false
    ) {
        DeleteMessage(screenState, onConfirmRequest)
    }
}

@Composable
private fun DeleteMessage(
    screenState: MainState,
    onConfirmRequest: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (screenState) {
                MainState.DECK_OVERVIEW -> DELETE_D_TEXT
                MainState.IN_QUESTION -> DELETE_Q_TEXT
                else -> ""
            },
            modifier = Modifier.padding(cardPadding),
            textAlign = TextAlign.Center
        )
        OutlinedButton(
            onClick = { onConfirmRequest() },
            modifier = Modifier.padding(cardPadding),
            border = BorderStroke(buttonStroke, MaterialTheme.colors.primary)
        ) {
            Text(text = CONFIRM_TEXT, style = MaterialTheme.typography.button)
        }
    }
}

@Preview
@Composable
private fun DeleteDeckDialogPreview() {
    SelfQuestTheme {
        Box(Modifier.size(dialogSize).border(buttonStroke, Color.Red)
        ) {
            DeleteMessage(MainState.DECK_OVERVIEW) {}
        }
    }
}

@Preview
@Composable
private fun DeleteQuestionDialogPreview() {
    SelfQuestTheme {
        Box(Modifier.size(dialogSize).border(buttonStroke, Color.Red)) {
            DeleteMessage(MainState.IN_QUESTION) {}
        }
    }
}