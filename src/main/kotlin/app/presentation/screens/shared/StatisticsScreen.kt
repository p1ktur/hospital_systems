package app.presentation.screens.shared

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import app.domain.model.shared.charts.*
import app.domain.tabNavigator.*
import app.domain.uiEvent.shared.*
import app.domain.uiState.shared.*
import app.presentation.codes.*
import app.presentation.components.charts.*

// Statistics

// Amount of appointments per day/week/month/year +
// Amount of hospitalizations per day/week/month/year +

// Current total salary per month +
// Current total salary per month % Current total amount of money earned from last month +

// Amount of money from appointments per day/week/month/year +
// Amount of money from hospitalizations per day/week/month/year +
// Amount of money from additional payments per day/week/month/year +
// Total amount of money from all sources per day/week/month/year +

// Room fullness: (total rooms) * 4 / (total hospitalizations) +
// Total amount of workers and registered clients (patients) +
// Best doctors by appointment earnings (can_receive_appointments = TRUE) +

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    uiState: StatisticsUiState,
    onUiEvent: (StatisticsUiEvent) -> Unit
) {
    val density = LocalDensity.current.density
    val screenWidthDp = (LocalWindowInfo.current.containerSize.width / density - 64).dp

    val rowState = rememberLazyListState()

    LaunchedEffect(key1 = uiState.displayMode, block = {
        rowState.animateScrollToItem(uiState.displayMode)
    })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                            onUiEvent(StatisticsUiEvent.UpdateDisplayMode(0))
                        }
                    )
                    .padding(8.dp),
                text = "Activity",
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
                            onUiEvent(StatisticsUiEvent.UpdateDisplayMode(1))
                        }
                    )
                    .padding(8.dp),
                text = "Expenses",
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
                            onUiEvent(StatisticsUiEvent.UpdateDisplayMode(2))
                        }
                    )
                    .padding(8.dp),
                text = "Income",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textDecoration = if (uiState.displayMode == 2) TextDecoration.Underline else TextDecoration.None,
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
                            onUiEvent(StatisticsUiEvent.UpdateDisplayMode(3))
                        }
                    )
                    .padding(8.dp),
                text = "Other",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textDecoration = if (uiState.displayMode == 3) TextDecoration.Underline else TextDecoration.None,
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
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            state = rowState,
            userScrollEnabled = false
        ) {
            // 0
            item {
                Column(
                    modifier = Modifier
                        .width(screenWidthDp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (uiState.errorCodes.contains(1001)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = parseDefaultErrorCode(1001),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    if (uiState.appointmentStatistics.containsData()) {
                        DataChart(
                            modifier = Modifier
                                .height(500.dp)
                                .aspectRatio(10f / 6f),
                            chartTimeData = uiState.appointmentStatistics.amountToChartTimeData(),
                            chartSettings = defaultChartSettings().copy(yTitle = "Amount")
                        )
                    } else {
                        Text(
                            text = "No data for appointments",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (uiState.hospitalizationStatistics.containsData()) {
                        DataChart(
                            modifier = Modifier
                                .height(500.dp)
                                .aspectRatio(10f / 6f),
                            chartTimeData = uiState.hospitalizationStatistics.amountToChartTimeData(),
                            chartSettings = defaultChartSettings().copy(yTitle = "Amount")
                        )
                    } else {
                        Text(
                            text = "No data for hospitalization",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            // 1
            item {
                Column(
                    modifier = Modifier
                        .width(screenWidthDp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (uiState.errorCodes.contains(1001)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = parseDefaultErrorCode(1001),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    if (uiState.salaryStatistics.containsData()) {
                        DataChart(
                            modifier = Modifier
                                .height(500.dp)
                                .aspectRatio(10f / 6f),
                            chartTimeData = uiState.salaryStatistics.amountToChartTimeData(),
                            chartSettings = defaultChartSettings().copy(yTitle = "Amount")
                        )
                    } else {
                        Text(
                            text = "No data for salary",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (uiState.salaryStatistics.containsData() && uiState.totalMoneyStatistics.containsData()) {
                        DataChart(
                            modifier = Modifier
                                .height(500.dp)
                                .aspectRatio(10f / 6f),
                            chartTimeData = uiState.salaryStatistics.amountPerMoneyEarnedToChartTimeData(uiState.totalMoneyStatistics),
                            chartSettings = defaultChartSettings().copy(yTitle = "Amount")
                        )
                    } else {
                        Text(
                            text = "No data for salary or total income",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            // 2
            item {
                Column(
                    modifier = Modifier
                        .width(screenWidthDp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (uiState.errorCodes.contains(1001)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = parseDefaultErrorCode(1001),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    if (uiState.appointmentStatistics.containsData()) {
                        DataChart(
                            modifier = Modifier
                                .height(500.dp)
                                .aspectRatio(10f / 6f),
                            chartTimeData = uiState.appointmentStatistics.moneyToChartTimeData(),
                            chartSettings = defaultChartSettings().copy(yTitle = "Amount")
                        )
                    } else {
                        Text(
                            text = "No data for appointments",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (uiState.hospitalizationStatistics.containsData()) {
                        DataChart(
                            modifier = Modifier
                                .height(500.dp)
                                .aspectRatio(10f / 6f),
                            chartTimeData = uiState.hospitalizationStatistics.moneyToChartTimeData(),
                            chartSettings = defaultChartSettings().copy(yTitle = "Amount")
                        )
                    } else {
                        Text(
                            text = "No data for hospitalizations",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (uiState.additionalPaymentStatistics.containsData()) {
                        DataChart(
                            modifier = Modifier
                                .height(500.dp)
                                .aspectRatio(10f / 6f),
                            chartTimeData = uiState.additionalPaymentStatistics.moneyToChartTimeData(),
                            chartSettings = defaultChartSettings().copy(yTitle = "Amount")
                        )
                    } else {
                        Text(
                            text = "No data for additional income",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (uiState.totalMoneyStatistics.containsData()) {
                        DataChart(
                            modifier = Modifier
                                .height(500.dp)
                                .aspectRatio(10f / 6f),
                            chartTimeData = uiState.totalMoneyStatistics.moneyToChartTimeData(),
                            chartSettings = defaultChartSettings().copy(yTitle = "Amount")
                        )
                    } else {
                        Text(
                            text = "No data for total income",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            // 3
            item {
                LazyColumn(
                    modifier = Modifier.width(screenWidthDp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Statistics",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (uiState.errorCodes.contains(1001)) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = parseDefaultErrorCode(1001),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        Text(
                            text = "Total working personnel: ${uiState.totalWorkers}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total patients: ${uiState.totalPatients}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp),
                            color = MaterialTheme.colorScheme.onBackground,
                            thickness = 1.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "Free beds: ${uiState.roomDataForStatistics.freeBeds}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "Busy beds: ${uiState.roomDataForStatistics.busyBeds}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = uiState.roomDataForStatistics.run {
                                    if (freeBeds + busyBeds == 0) {
                                        "Beds usage: 0"
                                    } else {
                                        "Beds usage: ${busyBeds / (freeBeds + busyBeds)}"
                                    }
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp),
                            color = MaterialTheme.colorScheme.onBackground,
                            thickness = 1.dp
                        )
                        if (uiState.registrationStatistics.containsData()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            DataChart(
                                modifier = Modifier
                                    .height(500.dp)
                                    .aspectRatio(10f / 6f),
                                chartTimeData = uiState.registrationStatistics.workersToChartTimeData(),
                                chartSettings = defaultChartSettings().copy(yTitle = "Amount")
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                                thickness = 1.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            DataChart(
                                modifier = Modifier
                                    .height(500.dp)
                                    .aspectRatio(10f / 6f),
                                chartTimeData = uiState.registrationStatistics.clientsToChartTimeData(),
                                chartSettings = defaultChartSettings().copy(yTitle = "Amount")
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                                thickness = 1.dp
                            )
                        } else {
                            Text(
                                text = "No data for registrations",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Best doctors by rating",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    itemsIndexed(uiState.bestDoctorsByAppointments) { index, doctor ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8))
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8))
                                .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8))
                                .padding(vertical = 12.dp, horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "${index + 1}. ${doctor.name} ${doctor.surname}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                if (doctor.login.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        modifier = Modifier.clickable(
                                            onClick = {
                                                navController.navigate("/info/worker/${doctor.userDoctorId}")
                                            }
                                        ),
                                        text = "@${doctor.login}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                }
                            }
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "Appointments: ${doctor.appointments}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Earned money: ${doctor.earnedMoney}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}