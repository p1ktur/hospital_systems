package app.presentation.theme

import androidx.compose.material3.*
import androidx.compose.ui.graphics.*

val defaultColorScheme = lightColorScheme()

val blueColorSchemeLight by lazy {
    defaultColorScheme.copy(
        primary = Color(0xFF276389),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFCAE6FF),
        onPrimaryContainer = Color(0xFF001E2F),
        secondary = Color(0xFF50606E),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFD3E5F5),
        onSecondaryContainer = Color(0xFF0C1D29),
        tertiary = Color(0xFF64597B),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFEADDFF),
        onTertiaryContainer = Color(0xFF201634),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        background = Color(0xFFF7F9FF),
        onBackground = Color(0xFF181C20),
        surface = Color(0xFFF7F9FF),
        onSurface = Color(0xFF181C20),
        surfaceVariant = Color(0xFFDDE3EA),
        onSurfaceVariant = Color(0xFF41474D),
        outline = Color(0xFF72787E),
        inverseSurface = Color(0xFF2D3135),
        inverseOnSurface = Color(0xFFEEF1F6),
        inversePrimary = Color(0xFF95CDF8)
    )
}

val blueColorSchemeDark by lazy {
    defaultColorScheme.copy(
        primary = Color(0xFF95CDF8),
        onPrimary = Color(0xFF00344E),
        primaryContainer = Color(0xFF004B6F),
        onPrimaryContainer = Color(0xFFCAE6FF),
        secondary = Color(0xFFB7C9D9),
        onSecondary = Color(0xFF22323F),
        secondaryContainer = Color(0xFF384956),
        onSecondaryContainer = Color(0xFFD3E5F5),
        tertiary = Color(0xFFCFC0E8),
        onTertiary = Color(0xFF352B4B),
        tertiaryContainer = Color(0xFF4C4162),
        onTertiaryContainer = Color(0xFFEADDFF),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        background = Color(0xFF101417),
        onBackground = Color(0xFFE0E3E8),
        surface = Color(0xFF101417),
        onSurface = Color(0xFFE0E3E8),
        surfaceVariant = Color(0xFF41474D),
        onSurfaceVariant = Color(0xFFC1C7CE),
        outline = Color(0xFF8B9198),
        inverseSurface = Color(0xFFE0E3E8),
        inverseOnSurface = Color(0xFF2D3135),
        inversePrimary = Color(0xFF276389)
    )
}