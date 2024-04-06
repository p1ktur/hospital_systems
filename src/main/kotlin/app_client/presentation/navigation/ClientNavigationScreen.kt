package app_client.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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

    TabNavigator(
        navOptions = listOf(
            TabNavOption(
                name = "Registration",
                route = "/registration"
            ),
            TabNavOption(
                name = "Login",
                route = "/login"
            ),
            TabNavOption(
                name = "Info",
                route = "/info"
            ),
            TabNavOption(
                name = "Appointments",
                route = "/appointments"
            ),
            TabNavOption(
                name = "Hospitalizations",
                route = "/hospitalizations"
            ),
            TabNavOption(
                name = "Payments",
                route = "/payments"
            )
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
            scene(route = "/registration") {
                val viewModel = koinViewModel<ClientRegistrationViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                ClientRegistrationScreen(
                    navigator = navigator,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    }
                )
            }
            scene(route = "/login") {

            }
            scene(route = "/info/{userId}") { navBackStackEntry ->
                val userId = navBackStackEntry.path<Int>("userId")
                val viewModel = koinViewModel<ClientInfoViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                ClientInfoScreen(
                    navigator = navigator,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    }
                )
            }
            scene(route = "/appointments") {

            }
            scene(route = "/hospitalizations") {

            }
            scene(route = "/payments") {

            }
        }
    }
}