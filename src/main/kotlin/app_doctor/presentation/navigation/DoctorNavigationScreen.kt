package app_doctor.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import app_client.domain.uiEvent.*
import app_client.domain.viewModel.*
import app_client.presentation.screens.*
import app_doctor.domain.uiEvent.*
import app_doctor.domain.viewModel.*
import app_doctor.presentation.screens.*
import app_shared.domain.model.args.*
import app_shared.domain.model.login.*
import app_shared.domain.model.result.*
import app_shared.domain.model.tabNavigator.*
import app_shared.domain.uiEvent.*
import app_shared.domain.viewModel.*
import app_shared.presentation.components.common.*
import app_shared.presentation.screens.*
import moe.tlaster.precompose.koin.*
import moe.tlaster.precompose.navigation.*

@Composable
fun DoctorNavigationScreen() {
    var isLoading by remember { mutableStateOf(false) }

    var loginStatus by remember { mutableStateOf<LoginStatus>(LoginStatus.LoggedOut) }
    var onLogOut by remember { mutableStateOf<(() -> Unit)?>(null) }

    if (loginStatus is LoginStatus.LoggedOut) {
        val viewModel = koinViewModel<DoctorLoginViewModel>()
        val uiState = viewModel.uiState.collectAsState()

        LaunchedEffect(key1 = true, block = {
            onLogOut = {
                viewModel.onUiEvent(LoginUiEvent.LogOut)
            }
        })

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
                route = "/info/worker/${(loginStatus as? LoginStatus.LoggedIn)?.userId}"
            ),
            TabNavOption(
                name = "Appointments",
                route = "/appointments/${(loginStatus as? LoginStatus.LoggedIn)?.userId}"
            ),
            TabNavOption(
                name = "Schedule",
                route = "/schedule/${(loginStatus as? LoginStatus.LoggedIn)?.userId}"
            ),
            TabNavOption(
                name = "Register patient",
                route = "/registration_patient"
            ),
            TabNavOption(
                name = "Find patient",
                route = "/find_patient/false"
            ),
        ),
        isLoading = isLoading,
        onLogOut = {
            loginStatus = LoginStatus.LoggedOut
            onLogOut?.invoke()
        }
    ) { navController ->
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navigator = navController.navigator,
            initialRoute = "/info/worker/${(loginStatus as? LoginStatus.LoggedIn)?.userId}"
        ) {
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
                    canEdit = false,
                    isRemote = false
                )
            }
            scene(route = "/appointments/{userDoctorId}") { navBackStackEntry ->
                val userDoctorId = navBackStackEntry.path<Int>("userDoctorId") ?: -1
                val viewModel = koinViewModel<AppointmentsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(AppointmentsUiEvent.FetchAppointmentsForDoctor(userDoctorId))
                })

                AppointmentsScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    userDoctorId = userDoctorId,
                    appArgs = AppArgs.DOCTOR
                )
            }
            scene(route = "/schedule/{userDoctorId}") { navBackStackEntry ->
                val userDoctorId = navBackStackEntry.path<Int>("userDoctorId") ?: -1
                val viewModel = koinViewModel<DoctorScheduleViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(DoctorScheduleUiEvent.FetchInfo(userDoctorId))
                })

                DoctorScheduleScreen(
                    uiState = uiState.value
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
            scene(route = "/find_patient/{forResult}") { navBackStackEntry ->
                val forResult = navBackStackEntry.path<Boolean>("forResult") ?: false
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
                    forResult = forResult
                )
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
        }
    }
}