package app_shared.presentation.theme

import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) redColorSchemeDark else redColorSchemeLight,
        typography = Typography,
        content = content
    )
}