package app_admin.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import app_admin.domain.viewModel.*
import app_admin.presentation.screens.*
import app_shared.domain.model.tabNavigator.*
import app_shared.presentation.components.*
import moe.tlaster.precompose.koin.*
import moe.tlaster.precompose.navigation.*

@Composable
fun AdminNavigationScreen() {
    val navigator = rememberNavigator()

    var isLoading by remember { mutableStateOf(false) }

    TabNavigator(
        navOptions = listOf(
            TabNavOption(
                name = "Register worker",
                route = "/registration"
            ),
//            TabNavOption(
//                name = "Appointments",
//                route = "/appointments/${(loginStatus as? ClientLoginStatus.LoggedIn)?.userClientId}"
//            ),
//            TabNavOption(
//                name = "Hospitalizations",
//                route = "/hospitalizations/${(loginStatus as? ClientLoginStatus.LoggedIn)?.userClientId}"
//            ),
//            TabNavOption(
//                name = "Payments",
//                route = "/payments/${(loginStatus as? ClientLoginStatus.LoggedIn)?.userClientId}"
//            )
        ),
        onNavigate = { route ->
            navigator.navigate(route)
        },
        isLoading = isLoading
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navigator = navigator,
            initialRoute = "/registration"
        ) {
//            scene(route = "/login") {
//                val viewModel = koinViewModel<ClientLoginViewModel>()
//                val uiState = viewModel.uiState.collectAsState()
//
//                LaunchedEffect(key1 = uiState.value.isLoading, block = {
//                    isLoading = uiState.value.isLoading
//                })
//
//                LaunchedEffect(key1 = uiState.value.clientLoginStatus, block = {
//                    loginStatus = uiState.value.clientLoginStatus
//                })
//
//                ClientLoginScreen(
//                    navigator = navigator,
//                    uiState = uiState.value,
//                    onUiEvent = { event ->
//                        viewModel.onUiEvent(event)
//                    }
//                )
//            }
            scene(route = "/registration") { navBackStackEntry ->
                val viewModel = koinViewModel<WorkerRegistrationViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                WorkerRegistrationScreen(
                    navigator = navigator,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    }
                )
            }
        }
    }
}