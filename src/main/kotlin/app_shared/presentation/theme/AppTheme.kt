package app_shared.presentation.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import app_shared.domain.model.theme.*

@Composable
fun AppTheme(
    theme: Theme,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = when (theme) {
            Theme.LIGHT -> blueColorSchemeLight
            Theme.DARK -> blueColorSchemeDark
        },
        typography = Typography,
        content = content
    )
}