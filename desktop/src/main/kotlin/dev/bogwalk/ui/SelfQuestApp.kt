package dev.bogwalk.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import dev.bogwalk.ui.components.LeftSideBar
import dev.bogwalk.ui.components.MainScreen
import dev.bogwalk.ui.style.SelfQuestTheme

@Composable
fun SelfQuestApp() {
    Row {
        LeftSideBar("Equine") {}
        MainScreen {}
    }
}

@Composable
@Preview
private fun SelfQuestAppPreview() {
    SelfQuestTheme {
        SelfQuestApp()
    }
}