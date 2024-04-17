package app.presentation.theme

import androidx.compose.material3.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*

val defaultTextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.5.sp
)

val Typography = Typography(
    bodySmall = defaultTextStyle.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    bodyMedium = defaultTextStyle.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = defaultTextStyle.copy(
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    titleSmall = defaultTextStyle.copy(
        fontSize = 24.sp,
        lineHeight = 28.sp
    ),
    titleMedium = defaultTextStyle.copy(
        fontSize = 32.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleLarge = defaultTextStyle.copy(
        fontSize = 40.sp,
        lineHeight = 44.sp,
        fontWeight = FontWeight.Bold
    )
)