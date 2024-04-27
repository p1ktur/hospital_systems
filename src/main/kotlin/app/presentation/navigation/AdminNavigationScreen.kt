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
import com.darkrockstudios.libraries.mpfilepicker.*
import moe.tlaster.precompose.koin.*
import moe.tlaster.precompose.navigation.*

@Composable
fun AdminNavigationScreen() {
    val adminViewModel = koinViewModel<AdminViewModel>()
    val adminViewModelIsLoading by remember { adminViewModel.isLoading }

    var showDirPickerForExportData by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = adminViewModelIsLoading, block = {
        isLoading = adminViewModelIsLoading
    })

    TabNavigator(
        navOptions = listOf(
            TabNavOption(
                name = "Appointments",
                route = "/appointments/null/null"
            ),
            TabNavOption(
                name = "Hospitalizations",
                route = "/hospitalizations/null/null"
            ),
            TabNavOption(
                name = "Payments",
                route = "/payments"
            ),
            TabNavOption(
                name = "Find worker",
                route = "/find_worker"
            ),
            TabNavOption(
                name = "Find patient",
                route = "/find_patient/false"
            ),
            TabNavOption(
                name = "Drugs",
                route = "/drugs"
            ),
            TabNavOption(
                name = "Rooms",
                route = "/find_room/false/true"
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
        menuOptions = listOf(
            MenuOption(
                text = "Export data",
                onClick = {
                    showDirPickerForExportData = true
                }
            ),
            MenuOption(
                text = "Regenerate data",
                showOnlyOnWelcomeScreen = true,
                onClick = {
                    adminViewModel.reInitializeDatabase()
                }
            )
        ),
        isLoading = isLoading,
        navigationAllowed = !adminViewModelIsLoading
    ) { navController ->
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navigator = navController.navigator,
            initialRoute = "/welcome"
        ) {
            scene(route = "/welcome") {
                WelcomeScreen(AppArgs.ADMIN)
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
            scene(route = "/appointments/null/{openId}") { navBackStackEntry ->
                val openId = navBackStackEntry.path<Int>("openId")
                val viewModel = koinViewModel<AppointmentsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(AppointmentsUiEvent.FetchAppointmentsForAdmin(openId))
                })

                AppointmentsScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    userDoctorId = null,
                    userClientId = null,
                    appArgs = AppArgs.ADMIN
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
                    appArgs = AppArgs.ADMIN
                )
            }
            scene(route = "/payments") {
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
                    appArgs = AppArgs.ADMIN
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
            scene(route = "/find_room/{forResult}/{all}") { navBackStackEntry ->
                val forResult = navBackStackEntry.path<Boolean>("forResult") ?: false
                val all = navBackStackEntry.path<Boolean>("all") ?: true
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
            scene(route = "/equipment") {
                val viewModel = koinViewModel<EquipmentsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(EquipmentsUiEvent.Search)
                })

                EquipmentsScreen(
                    navController = navController,
                    uiState = uiState.value,
                    onUiEvent = { event ->
                        viewModel.onUiEvent(event)
                    },
                    forResult = false
                )
            }
            scene(route = "/statistics") {
                val viewModel = koinViewModel<StatisticsViewModel>()
                val uiState = viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = uiState.value.isLoading, block = {
                    isLoading = uiState.value.isLoading
                })

                LaunchedEffect(key1 = true, block = {
                    viewModel.onUiEvent(StatisticsUiEvent.FetchStatisticsForAdmin)
                })

                StatisticsScreen(
                    navController = navController,
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

    DirectoryPicker(
        show = showDirPickerForExportData,
        initialDirectory = "C:/",
        title = "Choose folder to export data"
    ) { path ->
        showDirPickerForExportData = false

        if (path != null) adminViewModel.exportData(path)
    }
}