package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import dev.bogwalk.ui.style.*

@Composable
fun LeftSideBar(
    title: String?,
    onBackButtonClicked: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = Modifier.requiredWidth(sidebarWidth)
            .fillMaxHeight()
            .drawBehind {
                drawLine(
                    color = SelfQuestColors.primary,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = SIDEBAR_STROKE
                )
            }
    ) {
        Box(
            modifier = Modifier
                .requiredHeight(topBoxHeight)
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary)
        ) {
            title?.let {
                // should this be extracted?
                IconButton(
                    onClick = { onBackButtonClicked() },
                    modifier = Modifier.testTag(BACK_TAG).align(Alignment.TopStart).padding(buttonStroke)
                ) {
                    Icon(
                        painter = painterResource(BACK_ICON),
                        contentDescription = BACK_DESCRIPTION,
                        modifier = Modifier.requiredSize(iconSize),
                        tint = MaterialTheme.colors.onSurface
                    )
                }
                Text(
                    text = title,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(cardPadding),
                    style = MaterialTheme.typography.h4
                )
            }
        }
        content()
    }
}

@Composable
@Preview
fun LeftSideBarEmptyPreview() {
    SelfQuestTheme {
        Box(Modifier.requiredSize(preferredWidth.second)) {
            LeftSideBar(null, {}) {}
        }
    }
}

@Composable
@Preview
fun LeftSideBarDeckOverviewPreview() {
    SelfQuestTheme {
        Box(Modifier.requiredSize(preferredWidth.second)) {
            LeftSideBar("Equine", {}) {}
        }
    }
}