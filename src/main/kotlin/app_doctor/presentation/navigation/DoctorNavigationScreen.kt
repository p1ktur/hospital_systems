package app_doctor.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import app_client.domain.uiEvent.*
import app_client.domain.viewModel.*
import app_client.presentation.screens.*
import app_doctor.domain.model.*
import app_doctor.domain.viewModel.*
import app_doctor.presentation.screens.*
import app_shared.domain.model.result.*
import app_shared.domain.model.tabNavigator.*
import app_shared.presentation.components.*
import moe.tlaster.precompose.koin.*
import moe.tlaster.precompose.navigation.*

@Composable
fun DoctorNavigationScreen() {
    val navigator = rememberNavigator()

    var isLoading by remember { mutableStateOf(false) }
    val canGoBack by navigator.canGoBack.collectAsState(false)

    var loginStatus by remember { mutableStateOf<DoctorLoginStatus>(DoctorLoginStatus.LoggedOut) }

    if (loginStatus is DoctorLoginStatus.LoggedOut) {
        val viewModel = koinViewModel<DoctorLoginViewModel>()
        val uiState = viewModel.uiState.collectAsState()

        LaunchedEffect(key1 = uiState.value.isLoading, block = {
            isLoading = uiState.value.isLoading
        })

        LaunchedEffect(key1 = uiState.value.doctorLoginStatus, block = {
            loginStatus = uiState.value.doctorLoginStatus
        })

        DoctorLoginScreen(
            navigator = navigator,
            uiState = uiState.value,
            onUiEvent = { event ->
                viewModel.onUiEvent(event)
            }
        )
    } else TabNavigator(
        navOptions = listOf(
            TabNavOption(
                name = "Info",
                route = "/info/${(loginStatus as? DoctorLoginStatus.LoggedIn)?.userDoctorId}"
            ),
            TabNavOption(
                name = "Appointments",
                route = "/appointments/${(loginStatus as? DoctorLoginStatus.LoggedIn)?.userDoctorId}"
            ),
            TabNavOption(
                name = "Schedule",
                route = "/schedule/${(loginStatus as? DoctorLoginStatus.LoggedIn)?.userDoctorId}"
            ),
            TabNavOption(
                name = "Register patient",
                route = "/registration_patient"
            ),
            TabNavOption(
                name = "Find patient",
                route = "/find_patient"
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
            initialRoute = "/info/${(loginStatus as? DoctorLoginStatus.LoggedIn)?.userDoctorId}"
        ) {
            scene(route = "/info/{doctorId}") { navBackStackEntry ->

            }
            scene(route = "/appointments/{doctorId}") {

            }
            scene(route = "/schedule/{doctorId}") {

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
                            navigator.navigate("/info/patient/${(uiState.value.registrationResult as TaskResult.Success<*>).data as Int}")
                            viewModel.onUiEvent(ClientRegistrationUiEvent.ForgetRegistration)
                        }
                    }
                })

                ClientRegistrationScreen(
                    navigator = navigator,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    }
                )
            }
            scene(route = "/find_patient") {
                val viewModel = koinViewModel<FindClientViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(FindClientUiEvent.Search)
                })

                FindClientScreen(
                    navigator = navigator,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    }
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
                    navigator = navigator,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    userClientId = userClientId,
                    isRemote = true
                )
            }
        }
    }
}