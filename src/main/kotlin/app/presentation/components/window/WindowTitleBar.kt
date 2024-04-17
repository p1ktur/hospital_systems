package app.presentation.components.window

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
import app.domain.theme.*
import app.domain.util.args.*
import com.hospital.systems.hospitalsystems.generated.resources.*
import org.jetbrains.compose.resources.*

@Composable
fun WindowScope.WindowTitleBar(
    appArgs: AppArgs,
    theme: Theme,
    onChangeTheme: () -> Unit,
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
                    theme = theme,
                    onChangeTheme = onChangeTheme,
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
                        theme = theme,
                        onChangeTheme = onChangeTheme,
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
    theme: Theme,
    onChangeTheme: () -> Unit,
    onMinimize: () -> Unit,
    onMaximize: () -> Unit,
    onClose: () -> Unit
) {
    Row {
        Image(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .aspectRatio(1f),
            painter = painterResource(Res.drawable.app_icon),
            contentDescription = "App icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer, BlendMode.SrcAtop)
        )
        Text(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            text = "Hospital Systems" + when (appArgs) {
                AppArgs.CLIENT -> ". Client app"
                AppArgs.DOCTOR -> ". Doctor app"
                AppArgs.ADMIN -> ". Admin app"
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
                .clickable(onClick = onChangeTheme)
                .padding(4.dp),
            imageVector = when (theme) {
                Theme.LIGHT -> Icons.Default.LightMode
                Theme.DARK -> Icons.Default.DarkMode
            },
            contentDescription = "Change theme",
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
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