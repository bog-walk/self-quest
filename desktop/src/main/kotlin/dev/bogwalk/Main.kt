package dev.bogwalk

import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.bogwalk.ui.SelfQuestApp
import dev.bogwalk.ui.style.SQ_ICON
import dev.bogwalk.ui.style.SelfQuestTheme
import dev.bogwalk.ui.style.WINDOW_TITLE
import dev.bogwalk.client.SQAppState
import dev.bogwalk.ui.components.DeleteDialog
import dev.bogwalk.ui.components.WarningDialog

fun main() = application {
    val appState by remember { mutableStateOf(SQAppState()) }

    LaunchedEffect("initial load") {
        appState.loadSavedDecks()
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = WINDOW_TITLE,
        icon = painterResource(SQ_ICON)
    ) {
        SelfQuestTheme {
            if (appState.isDeleteDialogOpen) {
                DeleteDialog(appState.mainScreenState, appState::confirmDelete, appState::closeDeleteDialog)
            }
            if (appState.isWarningDialogOpen) {
                WarningDialog(appState::confirmLeaveForm, appState::closeWarningDialog)
            }
            SelfQuestApp(appState)
        }
    }
}