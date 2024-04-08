package app_client.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import app_client.domain.model.*
import app_client.domain.uiEvent.*
import app_client.domain.viewModel.*
import app_client.presentation.screens.*
import app_shared.domain.model.tabNavigator.*
import app_shared.presentation.components.*
import moe.tlaster.precompose.koin.*
import moe.tlaster.precompose.navigation.*

@Composable
fun ClientNavigationScreen() {
    val navigator = rememberNavigator()

    var isLoading by remember { mutableStateOf(false) }
    var isTabVisible by remember { mutableStateOf(false) }

    var loginStatus by remember { mutableStateOf<ClientLoginStatus>(ClientLoginStatus.LoggedOut) }

    LaunchedEffect(key1 = loginStatus, block = {
        when (loginStatus) {
            is ClientLoginStatus.LoggedIn -> {
                isTabVisible = true
                navigator.navigate("/info/${(loginStatus as ClientLoginStatus.LoggedIn).userClientId}")
            }
            ClientLoginStatus.LoggedOut -> Unit
        }
    })

    TabNavigator(
        navOptions = listOf(
            TabNavOption(
                name = "Info",
                route = "/info/${(loginStatus as? ClientLoginStatus.LoggedIn)?.userClientId}"
            ),
            TabNavOption(
                name = "Appointments",
                route = "/appointments/${(loginStatus as? ClientLoginStatus.LoggedIn)?.userClientId}"
            ),
            TabNavOption(
                name = "Hospitalizations",
                route = "/hospitalizations/${(loginStatus as? ClientLoginStatus.LoggedIn)?.userClientId}"
            ),
            TabNavOption(
                name = "Payments",
                route = "/payments/${(loginStatus as? ClientLoginStatus.LoggedIn)?.userClientId}"
            )
        ),
        onNavigate = { route ->
            navigator.navigate(route)
        },
        isLoading = isLoading,
        isVisible = isTabVisible
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navigator = navigator,
            initialRoute = "/login"
        ) {
            scene(route = "/login") {
                val viewModel = koinViewModel<ClientLoginViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = uiState.value.clientLoginStatus, block = {
                    loginStatus = uiState.value.clientLoginStatus
                })

                ClientLoginScreen(
                    navigator = navigator,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    }
                )
            }
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
                    navigator = navigator,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    }
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