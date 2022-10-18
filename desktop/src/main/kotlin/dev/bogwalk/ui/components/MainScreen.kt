package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.bogwalk.ui.style.SelfQuestTheme

@Composable
fun MainScreen(
    content: @Composable (BoxScope.() -> Unit)
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        VerticalMenu(Modifier.align(Alignment.BottomEnd), true, {}, {}, {})
        content()
    }
}

@Composable
@Preview
fun MainScreenWithListPreview() {
    SelfQuestTheme {
        MainScreen { QuestionList(List(3) { "This is a fake question." }) {} }
    }
}