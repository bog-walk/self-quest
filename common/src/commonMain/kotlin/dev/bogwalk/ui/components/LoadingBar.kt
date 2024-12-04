package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dev.bogwalk.ui.style.SelfQuestTheme
import dev.bogwalk.ui.style.midDp
import dev.bogwalk.ui.style.midWidth
import dev.bogwalk.ui.style.smallDp

const val LOADING_TEXT = "Loading collections"

@Composable
internal fun LoadingBar(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = LOADING_TEXT,
            modifier = Modifier.padding(midDp),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        LinearProgressIndicator(
            modifier = Modifier.padding(midDp)
                .size(width = midWidth, height = smallDp),
            color = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview
@Composable
private fun LoadingBarPreview() {
    SelfQuestTheme {
        LoadingBar(Modifier)
    }
}