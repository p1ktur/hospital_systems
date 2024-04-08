package app_doctor.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import app_doctor.domain.viewModel.*
import app_doctor.presentation.screens.*
import app_shared.domain.model.tabNavigator.*
import app_shared.presentation.components.*
import moe.tlaster.precompose.koin.*
import moe.tlaster.precompose.navigation.*

@Composable
fun DoctorNavigationScreen() {
    val navigator = rememberNavigator()

    var isLoading by remember { mutableStateOf(false) }

    TabNavigator(
        navOptions = listOf(
            TabNavOption(
                name = "Register client",
                route = "/registration"
            ),
//            TabNavOption(
//                name = "Info",
//                route = "/info"
//            ),
//            TabNavOption(
//                name = "Appointments",
//                route = "/appointments"
//            ),
//            TabNavOption(
//                name = "Hospitalizations",
//                route = "/hospitalizations"
//            ),
//            TabNavOption(
//                name = "Payments",
//                route = "/payments"
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
            initialRoute = "/login"
        ) {
            scene(route = "/login") {
                val viewModel = koinViewModel<DoctorLoginViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                DoctorLoginScreen(
                    navigator = navigator,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    }
                )
            }
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
            scene(route = "/info/{userId}") { navBackStackEntry ->

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