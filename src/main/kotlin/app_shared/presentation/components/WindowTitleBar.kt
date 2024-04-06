package app_shared.presentation.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import app_shared.domain.model.args.*
import app_shared.domain.model.tabNavigator.*
import com.hospital.systems.hospitalsystems.generated.resources.*
import org.jetbrains.compose.resources.*

@Composable
fun WindowScope.WindowTitleBar(
    appArgs: AppArgs,
    isMaximized: Boolean,
    onMinimize: () -> Unit,
    onMaximize: () -> Unit,
    onClose: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isMaximized) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TitleBarContent(
                    appArgs = appArgs,
                    onMinimize = onMinimize,
                    onMaximize = onMaximize,
                    onClose = onClose
                )
            }
        } else {
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
                    TitleBarContent(
                        appArgs = appArgs,
                        onMinimize = onMinimize,
                        onMaximize = onMaximize,
                        onClose = onClose
                    )
                }
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

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun TitleBarContent(
    appArgs: AppArgs,
    onMinimize: () -> Unit,
    onMaximize: () -> Unit,
    onClose: () -> Unit
) {
    Row {
        Image(
            modifier = Modifier
                .fillMaxHeight()
                .padding(4.dp)
                .aspectRatio(1f),
            painter = painterResource(Res.drawable.app_icon),
            contentDescription = "App icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer, BlendMode.SrcAtop)
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = when (appArgs) {
                AppArgs.CLIENT -> "Client app"
                AppArgs.DOCTOR -> "Doctor app"
                AppArgs.ADMIN -> "Admin app"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
    Row {
        Icon(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clickable(onClick = onMinimize)
                .padding(4.dp),
            imageVector = Icons.Default.Minimize,
            contentDescription = "Minimize app",
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Icon(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clickable(onClick = onMaximize)
                .padding(4.dp),
            imageVector = Icons.Default.Fullscreen,
            contentDescription = "Maximize app",
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
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