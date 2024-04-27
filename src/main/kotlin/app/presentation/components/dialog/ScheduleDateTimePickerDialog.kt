package app.presentation.components.dialog

import androidx.compose.desktop.ui.tooling.preview.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import app.domain.model.doctor.*
import app.domain.util.args.*
import app.domain.util.time.*
import app.domain.util.vocabulary.*
import app.presentation.components.common.*
import app.presentation.components.window.*
import app.presentation.theme.*
import java.time.LocalDateTime
import java.time.format.*
import java.util.*

@Composable
fun ScheduleDateTimePickerDialog(
    title: String? = null,
    appArgs: AppArgs,
    scheduleData: DoctorScheduleData,
    busyFutureDates: List<LocalDateTime>,
    visible: Boolean,
    onClose: () -> Unit,
    onDateTimePicked: (LocalDateTime) -> Unit
) {
    // TODO fix bugs

    val dialogState = rememberDialogState(size = DpSize(540.dp, 470.dp))
    val vocabulary by remember { mutableStateOf(Vocabulary()) }

    val currentTimeAsCalendar by remember {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY + 1))
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        mutableStateOf(calendar)
    }

    val year by remember { mutableStateOf(currentTimeAsCalendar.get(Calendar.YEAR)) }
    var selectedYear by remember { mutableStateOf(year) }
    val yearOptions = remember { mutableStateListOf(year, year + 1, year + 2) }

    val month by remember { mutableStateOf(currentTimeAsCalendar.get(Calendar.MONTH)) }
    var selectedMonth by remember { mutableStateOf(month) }
    val monthOptions = remember(selectedYear) {
        if (selectedYear == year) {
            List(12 - month) { index ->
                month + index
            }.toMutableStateList()
        } else {
            mutableStateListOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        }
    }

    val day by remember { mutableStateOf(currentTimeAsCalendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedDay by remember { mutableStateOf(day) }
    val dayOptions = remember(selectedYear, selectedMonth) {
        if (selectedYear == year && selectedMonth == month) {
            List(getMonthDays(month, year) + 2 - day) { index ->
                day + index - 1
            }
        } else {
            List(getMonthDays(selectedMonth, selectedYear)) { index ->
                index + 1
            }
        }.toMutableStateList()
    }

    val hour by remember { mutableStateOf(currentTimeAsCalendar.get(Calendar.HOUR_OF_DAY)) }
    val minute by remember { mutableStateOf(currentTimeAsCalendar.get(Calendar.MINUTE)) }
    var selectedHour by remember { mutableStateOf(hour) }
    val hourOptions = remember(selectedYear, selectedMonth, selectedDay) {
        println("here: ")
        println(selectedYear)
        println(selectedMonth)
        println(selectedDay)
        println("----------")
        if (selectedYear == year && selectedMonth == month && selectedDay == day) {
            List(24 - hour + if (minute in 30..60) 1 else 0) { index ->
                hour + index
            }
        } else {
            List(24) { index ->
                index
            }
        }.toMutableStateList()
    }

    var selectedMinute by remember { mutableStateOf(minute) }
    val minuteOptions = remember(selectedYear, selectedMonth, selectedDay, selectedHour) {
        if (selectedYear == year && selectedMonth == month && selectedDay == day && selectedHour == hour) {
            when (minute) {
                in 0..<30 -> {
                    listOf(30)
                }
                else -> listOf()
            }
        } else {
            listOf(0, 30)
        }.toMutableStateList()
    }

    val localDateTime by remember(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute) {
        println(selectedYear)
        println(selectedMonth + 1)
        println(selectedDay)
        println(selectedHour)
        println(selectedMinute)
        println("---------------")
        mutableStateOf(LocalDateTime.of(selectedYear, selectedMonth + 1, selectedDay, selectedHour, selectedMinute))
    }

    val isDateAvailable by remember(localDateTime) {
        val isValid = scheduleData.dateIsValid(vocabulary, localDateTime)
        val isNotBusy = busyFutureDates.find { compareDates(it, localDateTime) == 0 } == null

        mutableStateOf(isValid && isNotBusy)
    }

    val busyDaysFormatted = remember(busyFutureDates, localDateTime) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        busyFutureDates.filter { it.dayOfYear == localDateTime.dayOfYear }.map { it.format(formatter) }.toMutableStateList()
    }

    DialogWindow(
        state = dialogState,
        onCloseRequest = { onClose() },
        visible = visible,
        resizable = false,
        onKeyEvent = { event ->
            when (event.key) {
                Key.Escape -> onClose()
                else -> Unit
            }
            false
        },
        undecorated = true
    ) {
        DialogWindowTitleBar(
            title = title ?: "Select date and time",
            onClose = onClose
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                if (appArgs == AppArgs.DOCTOR) {
                    Text(
                        text = "You work on ${scheduleData.startDay} to ${scheduleData.endDay}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "from ${scheduleData.startTime} to ${scheduleData.endTime}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "resting from ${scheduleData.restStartTime} to ${scheduleData.restEndTime}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                } else if (appArgs == AppArgs.CLIENT) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "${scheduleData.doctorName} ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "works on ${scheduleData.startDay} to ${scheduleData.endDay}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = "from ${scheduleData.startTime} to ${scheduleData.endTime}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "resting from ${scheduleData.restStartTime} to ${scheduleData.restEndTime}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                if (busyDaysFormatted.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Busy time on that day: ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        busyDaysFormatted.forEachIndexed { index, it ->
                            Text(
                                text = it + if (index == busyDaysFormatted.size - 1) "" else ", ",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        modifier = Modifier.width(56.dp),
                        text = "Date: ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "(${localDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())})",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    OptionsTextField(
                        modifier = Modifier.width(100.dp),
                        startValue = year.toString(),
                        label = "",
                        options = yearOptions.map { it.toString() },
                        onOptionSelected = { index ->
                            selectedYear = yearOptions[index]
                        },
                        showEditIcon = false,
                        decorated = true
                    )
                    Text(
                        text = " / ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    OptionsTextField(
                        modifier = Modifier.width(160.dp),
                        startValue = month.toString(),
                        label = "",
                        options = monthOptions.map { parseMonth(it) },
                        onOptionSelected = { index ->
                            selectedMonth = monthOptions[index]
                        },
                        showEditIcon = false,
                        decorated = true
                    )
                    Text(
                        text = " / ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    OptionsTextField(
                        modifier = Modifier.width(80.dp),
                        startValue = day.toString(),
                        label = "",
                        options = dayOptions.map { it.toString() },
                        onOptionSelected = { index ->
                            selectedDay = dayOptions[index]
                        },
                        showEditIcon = false,
                        decorated = true
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.width(56.dp),
                    text = "Time: ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    OptionsTextField(
                        modifier = Modifier.width(100.dp),
                        startValue = hour.toString(),
                        label = "",
                        options = hourOptions.map { it.toString() },
                        onOptionSelected = { index ->
                            selectedHour = hourOptions[index]
                        },
                        showEditIcon = false,
                        decorated = true
                    )
                    Text(
                        text = " : ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    OptionsTextField(
                        modifier = Modifier.width(100.dp),
                        startValue = minute.toString(),
                        label = "",
                        options = minuteOptions.map { it.toString() },
                        onOptionSelected = { index ->
                            selectedMinute = minuteOptions[index]
                        },
                        showEditIcon = false,
                        decorated = true
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        modifier = Modifier.width(160.dp),
                        onClick = {
                            onDateTimePicked(localDateTime)
                        },
                        colors = buttonColors(),
                        enabled = isDateAvailable
                    ) {
                        Text(
                            text = "OK",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Button(
                        modifier = Modifier.width(160.dp),
                        onClick = onClose,
                        colors = buttonColors()
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

