package app_shared.presentation.components.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import app_shared.presentation.components.window.*

@Composable
fun DetailsDialog(
    size: DpSize,
    title: String,
    visible: Boolean,
    onClose: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val dialogState = rememberDialogState(size = size)

    DialogWindow(
        state = dialogState,
        onCloseRequest = { onClose() },
        visible = visible,
        resizable = false,
        onKeyEvent = { event ->
            when (event.key) {
                Key.Escape -> onClose()
                else -> Unit
            }
            false
        },
        undecorated = true
    ) {
        DialogWindowTitleBar(
            title = title,
            onClose = onClose
        ) {
            content()
        }
    }
}