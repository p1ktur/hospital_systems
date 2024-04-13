package app_shared.presentation.components.window

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*

@Composable
fun DialogWindowScope.DialogWindowTitleBar(
    title: String,
    onClose: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.onBackground)
    ) {
        WindowDraggableArea(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DialogTitleBarContent(
                    title = title,
                    onClose = onClose
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            content = content
        )
    }
}

@Composable
private fun DialogTitleBarContent(
    title: String,
    onClose: () -> Unit
) {
    Row {
        Icon(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .padding(4.dp),
            imageVector = Icons.Default.Info,
            contentDescription = "Info icon",
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 8.dp),
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
    Row {
        Icon(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clickable(onClick = onClose)
                .padding(4.dp),
            imageVector = Icons.Default.Close,
            contentDescription = "Close app",
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}