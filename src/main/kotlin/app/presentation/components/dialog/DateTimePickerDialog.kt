package app.presentation.components.dialog

import androidx.compose.desktop.ui.tooling.preview.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import app.presentation.components.common.*
import app.presentation.components.window.*
import app.presentation.theme.*
import java.util.*

@Composable
fun DateTimePickerDialog(
    title: String? = null,
    visible: Boolean,
    onClose: () -> Unit,
    onDateTimePicked: (Date) -> Unit
) {
    val dialogState = rememberDialogState(size = DpSize(500.dp, 320.dp))

    val currentTimeAsCalendar by remember { mutableStateOf(Calendar.getInstance()) }

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
            List(getMonthDays(month, year) - day) { index ->
                day + index
            }
        } else {
            List(getMonthDays(selectedMonth, selectedYear)) { index ->
                index + 1
            }
        }.toMutableStateList()
    }

    val hour by remember { mutableStateOf(currentTimeAsCalendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedHour by remember { mutableStateOf(hour) }
    val hourOptions = remember(selectedYear, selectedMonth, selectedDay) {
        if (selectedYear == year && selectedMonth == month && selectedDay == day) {
            List(24 - hour) { index ->
                hour + index
            }
        } else {
            List(24) { index ->
                index + 1
            }
        }.toMutableStateList()
    }

    val minute by remember { mutableStateOf(currentTimeAsCalendar.get(Calendar.MINUTE)) }
    var selectedMinute by remember { mutableStateOf(minute) }
    val minuteOptions = remember(selectedYear, selectedMonth, selectedDay, selectedHour) {
        if (selectedYear == year && selectedMonth == month && selectedDay == day && selectedMinute == minute) {
            List(60 - minute) { index ->
                minute + index
            }
        } else {
            List(60) { index ->
                index + 1
            }
        }.toMutableStateList()
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
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        modifier = Modifier.width(56.dp),
                        text = "Date: ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
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
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        modifier = Modifier.width(56.dp),
                        text = "Time: ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    OptionsTextField(
                        modifier = Modifier.width(80.dp),
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
                        modifier = Modifier.width(80.dp),
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
                            val calendar = Calendar.getInstance()
                            calendar.set(Calendar.YEAR, selectedYear)
                            calendar.set(Calendar.MONTH, selectedMonth)
                            calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                            calendar.set(Calendar.MINUTE, selectedMinute)

                            onDateTimePicked(calendar.time)
                        },
                        colors = buttonColors()
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

private fun parseMonth(monthIndex: Int): String {
    return when (monthIndex) {
        0 -> "January"
        1 -> "February"
        2 -> "March"
        3 -> "April"
        4 -> "May"
        5 -> "June"
        6 -> "July"
        7 -> "August"
        8 -> "September"
        9 -> "October"
        10 -> "November"
        else -> "December"
    }
}

private fun getMonthDays(monthIndex: Int, year: Int): Int {
    return when (monthIndex) {
        0, 2, 4, 6, 7, 9, 11 -> 31
        3, 5, 8, 10 -> 30
        else -> if (isLeapYear(year)) 29 else 28
    }
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0) && (year % 100 != 0 || year % 400 == 0)
}

@Preview
@Composable
fun PreviewDateTimePickerDialog() {
    DateTimePickerDialog(
        visible = true,
        onClose = {},
        onDateTimePicked = {}
    )
}