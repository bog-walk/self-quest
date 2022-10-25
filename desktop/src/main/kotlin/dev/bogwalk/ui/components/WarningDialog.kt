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

@Composable
fun WarningDialog(
    onConfirmRequest: () -> Unit,
    onCloseRequest: () -> Unit
) {
    Dialog(
        onCloseRequest = { onCloseRequest() },
        state = DialogState(WindowPosition(Alignment.Center), dialogSize),
        title = "",
        resizable = false
    ) {
        WarningMessage(onConfirmRequest)
    }
}

@Composable
private fun WarningMessage(
    onConfirmRequest: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = WARNING,
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
private fun WarningDialogPreview() {
    SelfQuestTheme {
        Box(
            Modifier.size(dialogSize).border(buttonStroke, Color.Red)
        ) {
            WarningMessage {}
        }
    }
}