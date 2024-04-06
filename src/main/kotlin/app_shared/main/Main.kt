package app_shared.main

import androidx.compose.foundation.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import app_client.di.*
import app_client.presentation.navigation.*
import app_shared.di.*
import app_shared.domain.model.args.*
import app_shared.domain.model.transactor.*
import app_shared.presentation.components.*
import app_shared.presentation.theme.*
import com.hospital.systems.hospitalsystems.generated.resources.*
import moe.tlaster.precompose.*
import org.jetbrains.compose.resources.*
import org.koin.compose.*
import org.koin.core.context.*
import org.koin.core.parameter.*
import java.awt.*

@OptIn(ExperimentalResourceApi::class)
fun main(vararg args: String) {
    val parsedArgs = parseArgs(args[0])

    startKoin {
        modules(
            sharedModule,
            clientModule
        )
    }

    application {
        val transactorForInit = koinInject<ITransactor>(parameters = { parametersOf(parsedArgs) })

        LaunchedEffect(key1 = true, block = {
            transactorForInit.checkStatus()
        })

        val windowState = rememberWindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(1080.dp, 720.dp)
        )

        var isMaximized by remember { mutableStateOf(false) }
        var isWindowResizeable by remember { mutableStateOf(true) }
        var lastPosition by remember { mutableStateOf(windowState.position) }
        var lastSize by remember { mutableStateOf(windowState.size) }

        Window(
            state = windowState,
            icon = painterResource(Res.drawable.app_icon),
            undecorated = true,
            resizable = isWindowResizeable,
            onCloseRequest = ::exitApplication
        ) {
            window.minimumSize = Dimension(1080, 720)

            PreComposeApp {
                AppTheme {
                    WindowTitleBar(
                        appArgs = parsedArgs,
                        isMaximized = isMaximized,
                        onMinimize = {
                            windowState.isMinimized = true
                        },
                        onMaximize = {
                            isMaximized = if (isMaximized) {
                                windowState.size = lastSize
                                windowState.position = lastPosition

                                isWindowResizeable = true
                                false
                            } else {
                                val insets = Toolkit.getDefaultToolkit().getScreenInsets(window.graphicsConfiguration)
                                val bounds = window.graphicsConfiguration.bounds

                                lastSize = windowState.size
                                lastPosition = windowState.position

                                windowState.size = DpSize(bounds.width.dp, (bounds.height - insets.bottom).dp)
                                windowState.position = WindowPosition(0.dp, 0.dp)

                                isWindowResizeable = false
                                true
                            }
                        },
                        onClose = {
                            exitApplication()
                        }
                    ) {
                        when (parsedArgs) {
                            AppArgs.CLIENT -> {
                                ClientNavigationScreen()
                            }
                            AppArgs.DOCTOR -> {

                            }
                            AppArgs.ADMIN -> {

                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseArgs(args: String): AppArgs {
    return when (args) {
        AppArgs.DOCTOR.value -> AppArgs.DOCTOR
        AppArgs.ADMIN.value -> AppArgs.ADMIN
        else -> AppArgs.CLIENT
    }
}
