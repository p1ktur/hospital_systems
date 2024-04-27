package app.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import app.domain.tabNavigator.*
import app.domain.uiEvent.client.*
import app.domain.uiEvent.doctor.*
import app.domain.uiEvent.shared.*
import app.domain.util.args.*
import app.domain.util.login.*
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
                name = "Schedule",
                route = "/schedule/${(loginStatus as? LoginStatus.LoggedIn)?.userId}"
            ),
            TabNavOption(
                name = "Appointments",
                route = "/appointments/${(loginStatus as? LoginStatus.LoggedIn)?.userId}/null"
            ),
            TabNavOption(
                name = "Hospitalizations",
                route = "/hospitalizations/null/null"
            ),
            TabNavOption(
                name = "Payments",
                route = "/payments/${(loginStatus as? LoginStatus.LoggedIn)?.userId}"
            ),
            TabNavOption(
                name = "Find patient",
                route = "/find_patient/false"
            ),
            TabNavOption(
                name = "Drugs",
                route = "/drugs"
            )
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
            initialRoute = "/welcome"
        ) {
            scene(route = "/welcome") {
                WelcomeScreen(AppArgs.DOCTOR)
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
                    canEdit = false,
                    isRemote = false
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
            scene(route = "/appointments/{userDoctorId}/{openId}") { navBackStackEntry ->
                val userDoctorId = navBackStackEntry.path<Int>("userDoctorId") ?: -1
                val openId = navBackStackEntry.path<Int>("openId")
                val viewModel = koinViewModel<AppointmentsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(AppointmentsUiEvent.FetchAppointmentsForDoctor(userDoctorId, openId))
                })

                AppointmentsScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    userDoctorId = userDoctorId,
                    userClientId = null,
                    appArgs = AppArgs.DOCTOR
                )
            }
            scene(route = "/hospitalizations/null/{openId}") { navBackStackEntry ->
                val openId = navBackStackEntry.path<Int>("openId")
                val viewModel = koinViewModel<HospitalizationsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(HospitalizationsUiEvent.FetchHospitalizationsForDoctorOrAdmin(openId))
                })

                HospitalizationsScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    appArgs = AppArgs.DOCTOR
                )
            }
            scene(route = "/payments/{userDoctorId}") {navBackStackEntry ->
                val userDoctorId = navBackStackEntry.path<Int>("userDoctorId") ?: -1
                val viewModel = koinViewModel<PaymentsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(PaymentsUiEvent.FetchPaymentsForDoctorOrAdmin)
                })

                PaymentsScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    appArgs = AppArgs.DOCTOR,
                    userDoctorId = userDoctorId
                )
            }
            scene(route = "/drugs") {
                val viewModel = koinViewModel<DrugsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(DrugsUiEvent.Search)
                })

                DrugsScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    forResult = false
                )
            }
            scene(route = "/find_patient/{forResult}") { navBackStackEntry ->
                val forResult = navBackStackEntry.path<Boolean>("forResult") ?: false
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
                    appArgs = AppArgs.DOCTOR,
                    forResult = forResult
                )
            }
            scene(route = "/registration_patient/{forResult}") { navBackStackEntry ->
                val forResult = navBackStackEntry.path<Boolean>("forResult") ?: false
                val viewModel = koinViewModel<ClientRegistrationViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
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
            scene(route = "/find_worker/{forResult}") { navBackStackEntry ->
                val forResult = navBackStackEntry.path<Boolean>("forResult") ?: false
                val viewModel = koinViewModel<DoctorsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(DoctorsUiEvent.Search(all = !forResult))
                })

                DoctorsScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    appArgs = AppArgs.DOCTOR,
                    forResult = forResult
                )
            }
            scene(route = "/find_room/{forResult}/{all}") { navBackStackEntry ->
                val forResult = navBackStackEntry.path<Boolean>("forResult") ?: false
                val all = navBackStackEntry.path<Boolean>("forResult") ?: true
                val viewModel = koinViewModel<RoomsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(RoomsUiEvent.Search(all = all))
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
        }
    }
}