package app_admin.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import app_admin.domain.viewModel.*
import app_admin.presentation.screens.*
import app_client.domain.uiEvent.*
import app_client.domain.viewModel.*
import app_client.presentation.screens.*
import app_doctor.domain.uiEvent.*
import app_doctor.domain.viewModel.*
import app_doctor.presentation.screens.*
import app_shared.domain.model.args.*
import app_shared.domain.model.result.*
import app_shared.domain.model.tabNavigator.*
import app_shared.domain.uiEvent.*
import app_shared.domain.viewModel.*
import app_shared.presentation.components.common.*
import app_shared.presentation.screens.*
import moe.tlaster.precompose.koin.*
import moe.tlaster.precompose.navigation.*

@Composable
fun AdminNavigationScreen() {
    var isLoading by remember { mutableStateOf(false) }

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
                route = "/find_patient/{forResult}"
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
        isLoading = isLoading
    ) { navController ->
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navigator = navController.navigator,
            initialRoute = "/registration_worker"
        ) {
            scene(route = "/registration_worker") {
                val viewModel = koinViewModel<WorkerRegistrationViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                WorkerRegistrationScreen(
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    }
                )
            }
            scene(route = "/find_worker") {
                val viewModel = koinViewModel<FindDoctorViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(FindDoctorUiEvent.Search)
                })

                FindDoctorScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    }
                )
            }
            scene(route = "/registration_patient") {
                val viewModel = koinViewModel<ClientRegistrationViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = uiState.value.registrationResult, block = {
                    when (uiState.value.registrationResult) {
                        TaskResult.NotCompleted -> Unit
                        TaskResult.Failure -> Unit
                        is TaskResult.Success<*> -> {
                            navController.navigate("/info/patient/${(uiState.value.registrationResult as TaskResult.Success<*>).data as Int}")
                            viewModel.onUiEvent(ClientRegistrationUiEvent.ForgetRegistration)
                        }
                    }
                })

                ClientRegistrationScreen(
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    }
                )
            }
            scene(route = "/find_patient/{forResult}") {
                val viewModel = koinViewModel<FindClientViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(FindClientUiEvent.Search)
                })

                FindClientScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    forResult = false
                )
            }
            scene(route = "/appointments") {
                val viewModel = koinViewModel<AppointmentsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(AppointmentsUiEvent.FetchAppointmentsForAdmin)
                })

                AppointmentsScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    userDoctorId = null,
                    appArgs = AppArgs.ADMIN
                )
            }
            scene(route = "/hospitalizations") {

            }
            scene(route = "/drugs") {

            }
            scene(route = "/rooms") {

            }
            scene(route = "/equipment") {

            }
            scene(route = "/statistics") {

            }
            scene(route = "/info/patient/{userClientId}") { navBackStackEntry ->
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
                    canEdit = true
                )
            }
            scene(route = "/info/worker/{userDoctorId}") { navBackStackEntry ->
                val userDoctorId = navBackStackEntry.path<Int>("userDoctorId") ?: -1
                val viewModel = koinViewModel<DoctorInfoViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(DoctorInfoUiEvent.FetchInfo(userDoctorId))
                })

                DoctorInfoScreen(
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    userDoctorId = userDoctorId,
                    canEdit = true,
                    isRemote = true
                )
            }
        }
    }
}