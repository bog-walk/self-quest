package dev.bogwalk.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.WindowPosition
import dev.bogwalk.models.MainState
import dev.bogwalk.ui.style.*

@Composable
fun WarningDialog(
    onConfirmRequest: () -> Unit,
    onCloseRequest: () -> Unit
) {
    SelfQuestDialog(onConfirmRequest, onCloseRequest) {
        Text(
            text = WARNING,
            modifier = Modifier.padding(cardPadding),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DeleteDialog(
    screenState: MainState,
    onConfirmRequest: () -> Unit,
    onCloseRequest: () -> Unit
) {
    SelfQuestDialog(onConfirmRequest, onCloseRequest) {
        Text(
            text = when (screenState) {
                MainState.DECK_OVERVIEW -> DELETE_D_TEXT
                MainState.IN_QUESTION -> DELETE_Q_TEXT
                MainState.IN_REVIEW -> DELETE_R_TEXT
                else -> ""
            },
            modifier = Modifier.padding(cardPadding),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SelfQuestDialog(
    onConfirmRequest: () -> Unit,
    onCloseRequest: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Dialog(
        onCloseRequest = { onCloseRequest() },
        state = DialogState(WindowPosition(Alignment.Center), dialogWidth, dialogHeight),
        title = "",
        resizable = false
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colors.surface).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
            DialogButton(onConfirmRequest)
        }
    }
}