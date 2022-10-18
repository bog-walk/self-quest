package dev.bogwalk

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.bogwalk.ui.SelfQuestApp
import dev.bogwalk.ui.style.SQ_ICON
import dev.bogwalk.ui.style.SelfQuestTheme
import dev.bogwalk.ui.style.WINDOW_TITLE

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = WINDOW_TITLE,
        icon = painterResource(SQ_ICON)
    ) {
        SelfQuestTheme {
            SelfQuestApp()
        }
    }
}