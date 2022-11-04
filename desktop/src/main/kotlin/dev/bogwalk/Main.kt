package dev.bogwalk

import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import dev.bogwalk.client.SQClient
import dev.bogwalk.ui.SelfQuestApp
import dev.bogwalk.ui.components.DeleteDialog
import dev.bogwalk.ui.components.WarningDialog
import dev.bogwalk.ui.style.*

fun main() = application {
    val scope = rememberCoroutineScope()
    val api by remember { mutableStateOf(SQClient(scope)) }

    LaunchedEffect("initial load") {
        api.loadSavedDecks()
    }

    Window(
        onCloseRequest = {
            api.cleanUp()
            exitApplication()
        },
        state = WindowState(width = minWindowWidth, height = minWindowHeight),
        title = WINDOW_TITLE,
        icon = painterResource(SQ_ICON)
    ) {
        SelfQuestTheme {
            if (api.isDeleteDialogOpen) {
                DeleteDialog(api.mainScreenState, api::confirmDelete, api::closeDeleteDialog)
            }
            if (api.isWarningDialogOpen) {
                WarningDialog(api::confirmLeaveForm, api::closeWarningDialog)
            }
            SelfQuestApp(api)
        }
    }
}