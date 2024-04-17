package app.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import app.domain.tabNavigator.*
import app.domain.uiEvent.client.*
import app.domain.uiEvent.doctor.*
import app.domain.uiEvent.shared.*
import app.domain.util.args.*
import app.domain.util.result.*
import app.domain.viewModel.client.*
import app.domain.viewModel.doctor.*
import app.domain.viewModel.shared.*
import app.presentation.components.common.*
import app.presentation.screens.client.*
import app.presentation.screens.doctor.*
import app.presentation.screens.shared.*
import moe.tlaster.precompose.koin.*
import moe.tlaster.precompose.navigation.*

@Composable
fun AdminNavigationScreen() {
    var isLoading by remember { mutableStateOf(false) }

    TabNavigator(
        navOptions = listOf(
            TabNavOption(
                name = "Find worker",
                route = "/find_worker"
            ),
            TabNavOption(
                name = "Find patient",
                route = "/find_patient/false"
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
                route = "/find_room/false"
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
            initialRoute = "/welcome"
        ) {
            scene(route = "/welcome") {
                WelcomeScreen(AppArgs.DOCTOR)
            }
            scene(route = "/registration_worker/{forResult}") { navBackStackEntry ->
                val forResult = navBackStackEntry.path<Boolean>("forResult") ?: false
                val viewModel = koinViewModel<DoctorRegistrationViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                DoctorRegistrationScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    forResult = forResult
                )
            }
            scene(route = "/find_worker") {
                val viewModel = koinViewModel<DoctorsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(DoctorsUiEvent.Search(all = true))
                })

                DoctorsScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    appArgs = AppArgs.ADMIN,
                    forResult = false
                )
            }
            scene(route = "/registration_patient/{forResult}") { navBackStackEntry ->
                val forResult = navBackStackEntry.path<Boolean>("forResult") ?: false
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
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    forResult = forResult
                )
            }
            scene(route = "/find_patient/{forResult}") {
                val viewModel = koinViewModel<ClientsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(ClientsUiEvent.Search)
                })

                ClientsScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    appArgs = AppArgs.ADMIN,
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
                val viewModel = koinViewModel<HospitalizationsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(HospitalizationsUiEvent.FetchHospitalizationsForDoctorOrAdmin)
                })

                HospitalizationsScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    appArgs = AppArgs.ADMIN
                )
            }
            scene(route = "/drugs") {

            }
            scene(route = "/find_room/{forResult}") { navBackStackEntry ->
                val forResult = navBackStackEntry.path<Boolean>("forResult") ?: false
                val viewModel = koinViewModel<RoomsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(RoomsUiEvent.Search(all = !forResult))
                })

                RoomsScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    forResult = forResult
                )
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