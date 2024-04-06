package app_shared.presentation.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun buttonColors() = ButtonDefaults.buttonColors(
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
)