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
    val canGoBack by navigator.canGoBack.collectAsState(false)

    TabNavigator(
        navOptions = listOf(
            TabNavOption(
                name = "Register worker",
                route = "/registration_worker"
            ),
            TabNavOption(
                name = "Find worker",
                route = "/find_worker"
            ),
            TabNavOption(
                name = "Register patient",
                route = "/registration_patient"
            ),
            TabNavOption(
                name = "Find patient",
                route = "/find_patient"
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
            ),
            TabNavOption(
                name = "Drugs",
                route = "/drugs"
            ),
            TabNavOption(
                name = "Rooms",
                route = "/rooms"
            ),
            TabNavOption(
                name = "Equipment",
                route = "/equipment"
            ),
            TabNavOption(
                name = "Statistics",
                route = "/statistics"
            ),
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
            initialRoute = "/registration_worker"
        ) {
            scene(route = "/registration_worker") {
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
            scene(route = "/find_worker") {

            }
            scene(route = "/registration_patient") {

            }
            scene(route = "/find_patient") {

            }
            scene(route = "/appointments") {

            }
            scene(route = "/hospitalizations") {

            }
            scene(route = "/payments") {

            }
            scene(route = "/drugs") {

            }
            scene(route = "/rooms") {

            }
            scene(route = "/equipment") {

            }
            scene(route = "/statistics") {

            }
        }
    }
}