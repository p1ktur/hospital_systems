package app_shared.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import app_shared.domain.model.forShared.hospitalization.*
import app_shared.domain.model.tabNavigator.*
import app_shared.domain.model.util.args.*
import app_shared.domain.uiEvent.*
import app_shared.domain.uiState.*
import app_shared.presentation.codes.*
import app_shared.presentation.components.common.*
import app_shared.presentation.components.dialog.*
import app_shared.presentation.components.hospitalizations.*
import kotlinx.coroutines.*

@Composable
fun HospitalizationsScreen(
    navController: NavController,
    uiState: HospitalizationsUiState,
    onUiEvent: (HospitalizationsUiEvent) -> Unit,
    appArgs: AppArgs
) {
    val coroutineScope = rememberCoroutineScope()

    var dialogHospitalization by remember { mutableStateOf<Hospitalization?>(null) }

    var navigatingToCreateAppointmentJob: Job? = null

    LaunchedEffect(key1 = uiState.hospitalizations, block = {
        if (dialogHospitalization != null) {
            dialogHospitalization = uiState.hospitalizations.find { it.id == dialogHospitalization?.id }
        }
    })

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = if (appArgs == AppArgs.ADMIN) "Your hospitalizations" else "Hospitalizations",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (uiState.errorCodes.contains(1001)) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = parseDefaultErrorCode(1001),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }
            if (uiState.hospitalizations.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.hospitalizations) { hospitalization ->
                        val payment by remember(hospitalization) {
                            mutableStateOf(uiState.payments.find { it.id == hospitalization.paymentId })
                        }

                        HospitalizationView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8))
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8))
                                .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8)),
                            hospitalization = hospitalization,
                            payment = payment,
                            appArgs = appArgs,
                            onClick = {
                                dialogHospitalization = hospitalization
                                onUiEvent(HospitalizationsUiEvent.ShowInfoDialog)
                            },
                            onClientNameClick = if (appArgs == AppArgs.CLIENT) null else ({
                                navController.navigate("/info/patient/${hospitalization.userClientId}")
                            }),
                        )
                    }
                }
            } else {
                Text(
                    text = "No hospitalizations yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
        HospitalizationInfoDialog(
            uiState = uiState,
            onUiEvent = onUiEvent,
            appArgs = appArgs,
            visible = uiState.showInfoDialog,
            dialogHospitalization = dialogHospitalization,
            onClose = {
                onUiEvent(HospitalizationsUiEvent.HideInfoDialog)
                if (uiState.editMode) onUiEvent(HospitalizationsUiEvent.DisableEditMode)
            }
        )
        if (appArgs == AppArgs.DOCTOR) {
            Row(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable(onClick = {
                            if (navigatingToCreateAppointmentJob == null) {
                                navigatingToCreateAppointmentJob = coroutineScope.launch {
                                    val userClientId = navController.navigateForResult("/find_patient/true") as Int?

                                    if (userClientId != null) {
                                        val roomId = navController.navigateForResult("/find_room/true") as Int?

                                        if (roomId != null) {
                                            onUiEvent(HospitalizationsUiEvent.StartCreatingHospitalization(userClientId, roomId))
                                        }
                                    }

                                    navigatingToCreateAppointmentJob = null
                                }
                            }
                        })
                        .padding(8.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add appointment",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun HospitalizationInfoDialog(
    uiState: HospitalizationsUiState,
    onUiEvent: (HospitalizationsUiEvent) -> Unit,
    appArgs: AppArgs,
    visible: Boolean,
    dialogHospitalization: Hospitalization?,
    onClose: () -> Unit
) {
    val payment by remember(dialogHospitalization) {
        mutableStateOf(
            if (dialogHospitalization != null) {
                uiState.payments.find { dialogHospitalization.paymentId == it.id }
            } else {
                null
            }
        )
    }

    val endDate by remember(dialogHospitalization) {
        mutableStateOf(dialogHospitalization?.endDate)
    }

    DetailsDialog(
        size = DpSize(600.dp, 400.dp),
        title = "Hospitalization Info",
        visible = visible,
        onClose = onClose
    ) {
        if (appArgs == AppArgs.DOCTOR) {
            if (uiState.creatingHospitalization) {
                var priceText by remember { mutableStateOf("") }
                var reasonText by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    DefaultTextField(
                        startValue = "",
                        label = "Price:",
                        onValueChange = { priceText = it },
                        onlyNumbers = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DefaultTextField(
                        startValue = "",
                        label = "Reason:",
                        onValueChange = { reasonText = it },
                        maxLength = 512,
                        multiLine = true
                    )
                }
                Icon(
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.TopEnd)
                        .clickable(onClick = {
                            if (uiState.userClientIdForHospitalization != null && priceText.isNotBlank() && reasonText.isNotBlank()) {
                                onUiEvent(
                                    HospitalizationsUiEvent.CreateHospitalization(
                                        reason = reasonText,
                                        price = priceText.toFloat()
                                    )
                                )
                                onClose()
                            }
                        })
                        .padding(8.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add hospitalization",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            } else if (dialogHospitalization != null) {
                var priceText by remember { mutableStateOf(dialogHospitalization.price.toString()) }
                var reasonText by remember { mutableStateOf(dialogHospitalization.reason) }

                if (uiState.editMode) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        if (payment == null) {
                            DefaultTextField(
                                startValue = priceText,
                                label = "Price:",
                                onValueChange = { priceText = it },
                                onlyNumbers = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        DefaultTextField(
                            startValue = reasonText,
                            label = "Reason:",
                            onValueChange = { reasonText = it },
                            maxLength = 512,
                            multiLine = true
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "Price: $priceText$",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Result: $reasonText",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (payment != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Payed ${payment?.payedAmount}\$ from ${payment?.payedAccount}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    if (endDate == null || endDate == "null") {
                        if (!uiState.editMode) {
                            Icon(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clickable(onClick = {
                                        onUiEvent(
                                            HospitalizationsUiEvent.EndHospitalization(
                                                hospitalizationId = dialogHospitalization.id
                                            )
                                        )
                                    })
                                    .padding(8.dp),
                                imageVector = Icons.Default.Done,
                                contentDescription = "End hospitalization",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Icon(
                            modifier = Modifier
                                .size(56.dp)
                                .clickable(onClick = {
                                    if ((uiState.editMode && reasonText.isNotBlank() && priceText.isNotBlank()) || !uiState.editMode) {
                                        onUiEvent(
                                            HospitalizationsUiEvent.ToggleEditMode(
                                                hospitalizationId = dialogHospitalization.id,
                                                reason = reasonText,
                                                price = priceText.toFloat()
                                            )
                                        )
                                    }
                                })
                                .padding(8.dp),
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit hospitalization",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                if (payment == null) {
                    Icon(
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.BottomEnd)
                            .clickable(onClick = {
                                onUiEvent(
                                    HospitalizationsUiEvent.DeleteHospitalization(
                                        hospitalizationId = dialogHospitalization.id
                                    )
                                )
                                onClose()
                            })
                            .padding(8.dp),
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete hospitalization",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        } else {
            if (appArgs == AppArgs.CLIENT) {
                if (dialogHospitalization != null) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Price: ${dialogHospitalization.price}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Reason: ${dialogHospitalization.reason}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (payment == null) {
                            Text(
                                text = "Your have not payed yet.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                modifier = Modifier.clickable(
                                    onClick = {
                                        onUiEvent(
                                            HospitalizationsUiEvent.PayForHospitalization(
                                                userClientId = dialogHospitalization.userClientId,
                                                hospitalizationId = dialogHospitalization.id,
                                                payedAmount = dialogHospitalization.price,
                                                payedAccount = "@${dialogHospitalization.clientLogin}'s Account"
                                            )
                                        )
                                        onClose()
                                    }
                                ),
                                text = "Click to pay",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.tertiary,
                                textDecoration = TextDecoration.Underline
                            )
                        } else {
                            Text(
                                text = "Your payment: ${payment?.payedAmount} from ${payment?.payedAccount}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Price: ${dialogHospitalization?.price}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Reason: ${dialogHospitalization?.reason}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (payment != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Payed ${payment?.payedAmount}$ from ${payment?.payedAccount}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}