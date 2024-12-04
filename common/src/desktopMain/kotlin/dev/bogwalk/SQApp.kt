package dev.bogwalk

import androidx.compose.runtime.*
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import dev.bogwalk.client.SQClient
import dev.bogwalk.common.generated.resources.Res
import dev.bogwalk.common.generated.resources.sq_icon
import dev.bogwalk.common.generated.resources.window_title
import dev.bogwalk.ui.SelfQuestApp
import dev.bogwalk.ui.components.DeleteDialog
import dev.bogwalk.ui.components.WarningDialog
import dev.bogwalk.ui.style.SelfQuestTheme
import dev.bogwalk.ui.style.minWindowHeight
import dev.bogwalk.ui.style.minWindowWidth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ApplicationScope.SQAppDesktop() {
    val scope = rememberCoroutineScope()
    val api by remember { mutableStateOf(SQClient(scope)) }

    LaunchedEffect("initial load") {
        api.isLoadingCollections = true
        withContext(Dispatchers.IO) {
            api.loadSavedDecks()
        }
        api.isLoadingCollections = false
    }

    Window(
        onCloseRequest = {
            api.cleanUp()
            exitApplication()
        },
        state = WindowState(width = minWindowWidth, height = minWindowHeight),
        title = stringResource(Res.string.window_title),
        icon = painterResource(Res.drawable.sq_icon)
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