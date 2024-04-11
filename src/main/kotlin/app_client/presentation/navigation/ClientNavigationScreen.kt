package app_client.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import app_client.domain.uiEvent.*
import app_client.domain.viewModel.*
import app_client.presentation.screens.*
import app_shared.domain.model.login.*
import app_shared.domain.model.tabNavigator.*
import app_shared.presentation.components.*
import app_shared.presentation.screens.*
import moe.tlaster.precompose.koin.*
import moe.tlaster.precompose.navigation.*

@Composable
fun ClientNavigationScreen() {
    val navigator = rememberNavigator()

    var isLoading by remember { mutableStateOf(false) }
    val canGoBack by navigator.canGoBack.collectAsState(false)

    var loginStatus by remember { mutableStateOf<LoginStatus>(LoginStatus.LoggedOut) }

    if (loginStatus is LoginStatus.LoggedOut) {
        val viewModel = koinViewModel<ClientLoginViewModel>()
        val uiState = viewModel.uiState.collectAsState()

        LaunchedEffect(key1 = uiState.value.isLoading, block = {
            isLoading = uiState.value.isLoading
        })

        LaunchedEffect(key1 = uiState.value.loginStatus, block = {
            loginStatus = uiState.value.loginStatus
        })

        LoginScreen(
            uiState = uiState.value,
            onUiEvent = { event ->
                viewModel.onUiEvent(event)
            }
        )
    } else TabNavigator(
        navOptions = listOf(
            TabNavOption(
                name = "Info",
                route = "/info/${(loginStatus as? LoginStatus.LoggedIn)?.userId}"
            ),
            TabNavOption(
                name = "Appointments",
                route = "/appointments/${(loginStatus as? LoginStatus.LoggedIn)?.userId}"
            ),
            TabNavOption(
                name = "Hospitalizations",
                route = "/hospitalizations/${(loginStatus as? LoginStatus.LoggedIn)?.userId}"
            ),
            TabNavOption(
                name = "Payments",
                route = "/payments/${(loginStatus as? LoginStatus.LoggedIn)?.userId}"
            )
        ),
        onNavigate = { route ->
            navigator.navigate(route)
        },
        onNavigateBack = {
            navigator.goBack()
        },
        canGoBack = canGoBack,
        isLoading = isLoading
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navigator = navigator,
            initialRoute = "/info/${(loginStatus as? LoginStatus.LoggedIn)?.userId}"
        ) {
            scene(route = "/info/{userClientId}") { navBackStackEntry ->
                val userClientId = navBackStackEntry.path<Int>("userClientId") ?: -1
                val viewModel = koinViewModel<ClientInfoViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(ClientInfoUiEvent.FetchInfo(userClientId))
                })

                ClientInfoScreen(
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    userClientId = userClientId,
                    isRemote = false
                )
            }
            scene(route = "/appointments/{userClientId}") { navBackStackEntry ->
                val userClientId = navBackStackEntry.path<Int>("userClientId") ?: -1
//                val viewModel = koinViewModel<ClientInfoViewModel>()
//                val uiState = viewModel.uiState.collectAsState()
//
//                ClientInfoScreen(
//                    navigator = navigator,
//                    uiState = uiState.value,
//                    onUiEvent = { event ->
//                        viewModel.onUiEvent(event)
//                    }
//                )
            }
            scene(route = "/hospitalizations/{userClientId}") { navBackStackEntry ->
                val userClientId = navBackStackEntry.path<Int>("userClientId") ?: -1
//                val viewModel = koinViewModel<ClientInfoViewModel>()
//                val uiState = viewModel.uiState.collectAsState()
//
//                ClientInfoScreen(
//                    navigator = navigator,
//                    uiState = uiState.value,
//                    onUiEvent = { event ->
//                        viewModel.onUiEvent(event)
//                    }
//                )
            }
            scene(route = "/payments/{userClientId}") { navBackStackEntry ->
                val userClientId = navBackStackEntry.path<Int>("userClientId") ?: -1
//                val viewModel = koinViewModel<ClientInfoViewModel>()
//                val uiState = viewModel.uiState.collectAsState()
//
//                ClientInfoScreen(
//                    navigator = navigator,
//                    uiState = uiState.value,
//                    onUiEvent = { event ->
//                        viewModel.onUiEvent(event)
//                    }
//                )
            }
        }
    }
}