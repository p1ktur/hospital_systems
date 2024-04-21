package app.presentation.screens.shared

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
import app.domain.model.shared.payment.*
import app.domain.tabNavigator.*
import app.domain.uiEvent.shared.*
import app.domain.uiState.shared.*
import app.domain.util.args.*
import app.presentation.codes.*
import app.presentation.components.common.*
import app.presentation.components.dialog.*
import app.presentation.components.payments.*
import kotlinx.coroutines.*

@Composable
fun PaymentsScreen(
    navController: NavController,
    uiState: PaymentsUiState,
    onUiEvent: (PaymentsUiEvent) -> Unit,
    appArgs: AppArgs,
    userClientId: Int? = null,
    userDoctorId: Int? = null
) {
    val coroutineScope = rememberCoroutineScope()

    var dialogPayment by remember { mutableStateOf<Payment.Sub?>(null) }

    var navigatingToCreateSubPaymentJob: Job? = null

    LaunchedEffect(key1 = uiState.subPayments, block = {
        when (dialogPayment) {
            is Payment.Sub -> dialogPayment = uiState.subPayments.find { it.id == (dialogPayment as Payment.Sub).id }
            else -> Unit
        }
    })

    LaunchedEffect(key1 = appArgs, block = {
        if (appArgs == AppArgs.DOCTOR) {
            onUiEvent(PaymentsUiEvent.UpdateDisplayMode(1))
        }
    })

    LaunchedEffect(key1 = uiState.displayMode, block = {
        onUiEvent(PaymentsUiEvent.UpdatePageTitle(
            when (uiState.displayMode) {
                0 -> if (appArgs == AppArgs.CLIENT) "Your payments" else "Payments"
                1 -> if (appArgs == AppArgs.CLIENT) "Your additional payments" else "Additional payments"
                2 -> if (appArgs == AppArgs.CLIENT) "All your payments" else "All payments"
                else -> return@LaunchedEffect
            }
        ))
    })

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (appArgs != AppArgs.DOCTOR) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                onClick = {
                                    onUiEvent(PaymentsUiEvent.UpdateDisplayMode(0))
                                }
                            )
                            .padding(8.dp),
                        text = "Payments",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        textDecoration = if (uiState.displayMode == 0) TextDecoration.Underline else TextDecoration.None,
                        textAlign = TextAlign.Center
                    )
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight(),
                        color = MaterialTheme.colorScheme.onBackground,
                        thickness = 1.dp
                    )
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                onClick = {
                                    onUiEvent(PaymentsUiEvent.UpdateDisplayMode(1))
                                }
                            )
                            .padding(8.dp),
                        text = "Additional",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        textDecoration = if (uiState.displayMode == 1) TextDecoration.Underline else TextDecoration.None,
                        textAlign = TextAlign.Center
                    )
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight(),
                        color = MaterialTheme.colorScheme.onBackground,
                        thickness = 1.dp
                    )
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                onClick = {
                                    onUiEvent(PaymentsUiEvent.UpdateDisplayMode(2))
                                }
                            )
                            .padding(8.dp),
                        text = "All",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        textDecoration = if (uiState.displayMode == 2) TextDecoration.Underline else TextDecoration.None,
                        textAlign = TextAlign.Center
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    thickness = 1.dp
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = uiState.pageTitle,
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
            when (uiState.displayMode) {
                0 -> {
                    if (uiState.payments.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.payments) { payment ->
                                PaymentView(
                                    payment = payment,
                                    appArgs = appArgs,
                                    isSelected = false,
                                    onClick = {
                                        val id = when (appArgs) {
                                            AppArgs.CLIENT -> userClientId
                                            AppArgs.DOCTOR -> userDoctorId
                                            AppArgs.ADMIN -> null
                                        }
                                        when (payment.helpIdType) {
                                            0 -> navController.navigate("/appointments/$id/${payment.helpId}")
                                            1 -> navController.navigate("/hospitalizations/$id/${payment.helpId}")
                                        }
                                    },
                                    onClientNameClick = if (appArgs == AppArgs.CLIENT) null else ({
                                        navController.navigate("/info/patient/${payment.userClientId}")
                                    }),
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No payments yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                1 -> {
                    if (uiState.subPayments.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.subPayments) { subPayment ->
                                PaymentView(
                                    payment = subPayment,
                                    appArgs = appArgs,
                                    isSelected = dialogPayment == subPayment,
                                    onClick = {
                                        dialogPayment = subPayment
                                        onUiEvent(PaymentsUiEvent.ShowInfoDialog)
                                    },
                                    onClientNameClick = if (appArgs == AppArgs.CLIENT) null else ({
                                        navController.navigate("/info/patient/${subPayment.userClientId}")
                                    }),
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No additional payments yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                2 -> {
                    val allPayments by remember(uiState.payments, uiState.subPayments) {
                        mutableStateOf(uiState.payments + uiState.subPayments)
                    }

                    if (allPayments.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(allPayments) { payment ->
                                PaymentView(
                                    payment = payment,
                                    appArgs = appArgs,
                                    isSelected = when (payment) {
                                        is Payment.Default -> false
                                        is Payment.Sub -> dialogPayment == payment
                                    },
                                    onClick = {
                                        val id = when (appArgs) {
                                            AppArgs.CLIENT -> userClientId
                                            AppArgs.DOCTOR -> userDoctorId
                                            AppArgs.ADMIN -> null
                                        }
                                        when (payment) {
                                            is Payment.Default -> when (payment.helpIdType) {
                                                0 -> navController.navigate("/appointments/$id/${payment.helpId}")
                                                1 -> navController.navigate("/hospitalizations/$id/${payment.helpId}")
                                            }
                                            is Payment.Sub -> {
                                                dialogPayment = payment
                                                onUiEvent(PaymentsUiEvent.ShowInfoDialog)
                                            }
                                        }
                                    },
                                    onClientNameClick = if (appArgs == AppArgs.CLIENT) null else ({
                                        navController.navigate("/info/patient/${payment.userClientId}")
                                    }),
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No payments yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
        PaymentInfoDialog(
            uiState = uiState,
            onUiEvent = onUiEvent,
            appArgs = appArgs,
            visible = uiState.showInfoDialog,
            userClientId = userClientId,
            dialogPayment = dialogPayment,
            onClose = {
                onUiEvent(PaymentsUiEvent.HideInfoDialog)
                if (uiState.editMode) onUiEvent(PaymentsUiEvent.DisableEditMode)
            }
        )
        if (appArgs != AppArgs.CLIENT) {
            Row(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable(onClick = {
                            if (navigatingToCreateSubPaymentJob == null) {
                                navigatingToCreateSubPaymentJob = coroutineScope.launch {
                                    val chosenUserClientId = navController.navigateForResult("/find_patient/true") as Int?

                                    if (chosenUserClientId != null) {
                                        onUiEvent(PaymentsUiEvent.StartCreatingSubPayment(chosenUserClientId))
                                    }

                                    navigatingToCreateSubPaymentJob = null
                                }
                            }
                        })
                        .padding(8.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add additional payment",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun PaymentInfoDialog(
    uiState: PaymentsUiState,
    onUiEvent: (PaymentsUiEvent) -> Unit,
    appArgs: AppArgs,
    visible: Boolean,
    userClientId: Int?,
    dialogPayment: Payment.Sub?,
    onClose: () -> Unit
) {
    DetailsDialog(
        size = DpSize(600.dp, 400.dp),
        title = "Payment Info",
        visible = visible,
        onClose = onClose
    ) {
        if (appArgs == AppArgs.DOCTOR || appArgs == AppArgs.ADMIN) {
            if (uiState.creatingSubPayment) {
                var amountText by remember { mutableStateOf("") }
                var subjectText by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    DefaultTextField(
                        startValue = "",
                        label = "Amount:",
                        onValueChange = { amountText = it },
                        onlyNumbers = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DefaultTextField(
                        startValue = "",
                        label = "Subject:",
                        onValueChange = { subjectText = it },
                        maxLength = 512,
                        multiLine = true
                    )
                }
                Icon(
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.TopEnd)
                        .clickable(onClick = {
                            if (uiState.userClientIdForSubPayment != null && amountText.isNotBlank() && subjectText.isNotBlank()) {
                                onUiEvent(
                                    PaymentsUiEvent.CreateSubPayment(
                                        subject = subjectText,
                                        amount = amountText.toFloat()
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
            } else if (dialogPayment != null) {
                var amountText by remember { mutableStateOf(dialogPayment.toPayAmount.toString()) }
                var subjectText by remember { mutableStateOf(dialogPayment.subject) }

                if (uiState.editMode) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        if (dialogPayment.payedAmount == 0f) {
                            DefaultTextField(
                                startValue = amountText,
                                label = "Amount:",
                                onValueChange = { amountText = it },
                                onlyNumbers = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        } else {
                            Text(
                                text = "Amount: $amountText$",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        DefaultTextField(
                            startValue = subjectText,
                            label = "Subject:",
                            onValueChange = { subjectText = it },
                            maxLength = 512,
                            multiLine = true
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "Amount: $amountText$",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Subject: $subjectText",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (dialogPayment.payedAmount != 0f) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Payed ${dialogPayment.payedAmount}\$ from ${dialogPayment.payedAccount}",
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
                    Icon(
                        modifier = Modifier
                            .size(56.dp)
                            .clickable(onClick = {
                                if ((uiState.editMode && subjectText.isNotBlank() && amountText.isNotBlank()) || !uiState.editMode) {
                                    onUiEvent(
                                        PaymentsUiEvent.ToggleEditMode(
                                            subPaymentId = dialogPayment.id,
                                            subject = subjectText,
                                            amount = amountText.toFloat()
                                        )
                                    )
                                }
                            })
                            .padding(8.dp),
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit payment",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Icon(
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.BottomEnd)
                        .clickable(onClick = {
                            onUiEvent(
                                PaymentsUiEvent.DeleteSubPayment(
                                    subPaymentId = dialogPayment.id
                                )
                            )
                            onClose()
                        })
                        .padding(8.dp),
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete payment",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        } else if (appArgs == AppArgs.CLIENT) {
            if (dialogPayment != null) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Amount: ${dialogPayment.toPayAmount}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Subject: ${dialogPayment.subject}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (dialogPayment.payedAmount <= 0f) {
                        Text(
                            text = "Your have not payed yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            modifier = Modifier.clickable(
                                onClick = {
                                    if (userClientId != null) {
                                        onUiEvent(
                                            PaymentsUiEvent.PayForSubPayment(
                                                userClientId = userClientId,
                                                subPaymentId = dialogPayment.id,
                                                payedAmount = dialogPayment.toPayAmount,
                                                payedAccount = "@${dialogPayment.clientLogin}'s Account"
                                            )
                                        )
                                    }
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
                            text = "Your payment: ${dialogPayment.payedAmount} from ${dialogPayment.payedAccount}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}