package app.main

import androidx.compose.foundation.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import app.data.core.*
import app.di.*
import app.domain.database.transactor.*
import app.domain.theme.*
import app.domain.util.args.*
import app.presentation.components.window.*
import app.presentation.navigation.*
import app.presentation.theme.*
import com.hospital.systems.hospitalsystems.generated.resources.*
import kotlinx.coroutines.*
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
            clientModule,
            doctorModule,
            adminModule
        )
    }

    application {
        val coroutineScope = rememberCoroutineScope()
        val transactorForInit = koinInject<ITransactor>(parameters = { parametersOf(parsedArgs) })

        LaunchedEffect(key1 = true, block = {
            transactorForInit.checkStatus()
        })

        if (args.asList().contains("init_db")) {
            val databaseInitializer = koinInject<DatabaseInitializer>()
            coroutineScope.launch(Dispatchers.IO) {
                databaseInitializer.initializeDatabaseWithResetOnFailure()
            }
            return@application
        }

        val windowState = rememberWindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(1080.dp, 720.dp)
        )

        val isSystemInDarkTheme = isSystemInDarkTheme()
        var appTheme by remember { mutableStateOf(if (isSystemInDarkTheme) Theme.DARK else Theme.LIGHT) }

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
                AppTheme(appTheme) {
                    WindowTitleBar(
                        appArgs = parsedArgs,
                        theme = appTheme,
                        onChangeTheme = {
                            appTheme = !appTheme
                        },
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
                                DoctorNavigationScreen()
                            }
                            AppArgs.ADMIN -> {
                                AdminNavigationScreen()
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
