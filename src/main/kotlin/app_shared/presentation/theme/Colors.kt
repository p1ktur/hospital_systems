package app_shared.presentation.theme

import androidx.compose.material3.*
import androidx.compose.ui.graphics.*

val defaultColorScheme = lightColorScheme()

val redColorSchemeLight by lazy {
    defaultColorScheme.copy(
        primary = Color(0xFF8F4A4F),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFDADA),
        onPrimaryContainer = Color(0xFF3B0810),
        secondary = Color(0xFF765657),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFFFDADA),
        onSecondaryContainer = Color(0xFF2C1516),
        tertiary = Color(0xFF76592F),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFDDB2),
        onTertiaryContainer = Color(0xFF291800),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        background = Color(0xFFFFF8F7),
        onBackground = Color(0xFF22191A),
        surface = Color(0xFFFFF8F7),
        onSurface = Color(0xFF22191A),
        surfaceVariant = Color(0xFFF4DDDD),
        onSurfaceVariant = Color(0xFF524343),
        outline = Color(0xFF857373),
        inverseSurface = Color(0xFF382E2E),
        inverseOnSurface = Color(0xFFFFEDEC),
        inversePrimary = Color(0xFFFFB3B6)
    )
}

val redColorSchemeDark by lazy {
    defaultColorScheme.copy(
        primary = Color(0xFFFFB3B6),
        onPrimary = Color(0xFF561D23),
        primaryContainer = Color(0xFF723338),
        onPrimaryContainer = Color(0xFFFFDADA),
        secondary = Color(0xFFE6BDBE),
        onSecondary = Color(0xFF44292B),
        secondaryContainer = Color(0xFF5D3F40),
        onSecondaryContainer = Color(0xFFFFDADA),
        tertiary = Color(0xFFE6C08D),
        onTertiary = Color(0xFF432C06),
        tertiaryContainer = Color(0xFF5C421A),
        onTertiaryContainer = Color(0xFFFFDDB2),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        background = Color(0xFF1A1111),
        onBackground = Color(0xFFF0DEDE),
        surface = Color(0xFF1A1111),
        onSurface = Color(0xFFF0DEDE),
        surfaceVariant = Color(0xFF524343),
        onSurfaceVariant = Color(0xFFD7C1C2),
        outline = Color(0xFF9F8C8C),
        inverseSurface = Color(0xFFF0DEDE),
        inverseOnSurface = Color(0xFF382E2E),
        inversePrimary = Color(0xFF8F4A4F)
    )
}