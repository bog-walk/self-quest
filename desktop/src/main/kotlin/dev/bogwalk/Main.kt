package dev.bogwalk

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        resizable = false
    ) {
        MaterialTheme {
            Text("Hello World!")
        }
    }
}