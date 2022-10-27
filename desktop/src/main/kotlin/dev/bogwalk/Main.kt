package dev.bogwalk

import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.bogwalk.client.MockAppState
import dev.bogwalk.ui.SelfQuestApp
import dev.bogwalk.ui.components.DeleteDialog
import dev.bogwalk.ui.components.WarningDialog
import dev.bogwalk.ui.style.*

fun main() = application {
    //val appState by remember { mutableStateOf(SQAppState()) }
    val appState by remember { mutableStateOf(MockAppState()) }

    LaunchedEffect("initial load") {
        appState.loadSavedDecks()
    }

    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(width = minWindowWidth, height = minWindowHeight),
        title = WINDOW_TITLE,
        icon = painterResource(SQ_ICON)
    ) {
        SelfQuestTheme {
            if (appState.isDeleteDialogOpen.value) {
                DeleteDialog(appState.mainScreenState.value, appState::confirmDelete, appState::closeDeleteDialog)
            }
            if (appState.isWarningDialogOpen.value) {
                WarningDialog(appState::confirmLeaveForm, appState::closeWarningDialog)
            }
            SelfQuestApp(appState)
        }
    }
}