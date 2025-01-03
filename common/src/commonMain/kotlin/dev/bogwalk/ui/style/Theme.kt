package dev.bogwalk.ui.style

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val SelfQuestColors = darkColorScheme(
    primary = Color(0xff82b58a), // green
    secondary = Color(0xffdedede),  // light grey
    background = Color(0xfff8f8ff),  // ghostwhite
    surface = Color(0xff212121),  // dark grey
    error = Color(0xffe68484),  // red
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color(0xfff8f8ff),  // ghost white
    onError = Color.Black
)

val SelfQuestScrollBar = ScrollbarStyle(
    minimalHeight = 16.dp,
    thickness = 8.dp,
    shape = RoundedCornerShape(4),
    hoverDurationMillis = 300,
    unhoverColor = Color.Black.copy(alpha = 0.12f),
    hoverColor = SelfQuestColors.primary
)

private val SelfQuestTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 34.sp,
        color = SelfQuestColors.onSurface,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Left
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 24.sp,
        color = SelfQuestColors.primary,
        fontWeight = FontWeight.ExtraBold
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Left,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        color = SelfQuestColors.onSurface,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Left,
        lineHeight = 24.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        color = SelfQuestColors.onSurface,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
    )
)

val SelfQuestShapes = Shapes(
    medium = RoundedCornerShape(7),
    large = RoundedCornerShape(10)
)

@Composable
fun SelfQuestTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SelfQuestColors,
        typography = SelfQuestTypography,
        shapes = SelfQuestShapes
    ) {
        Surface(content = content)
    }
}