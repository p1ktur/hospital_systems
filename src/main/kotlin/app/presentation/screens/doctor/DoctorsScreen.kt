package app.presentation.screens.doctor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import app.domain.model.doctor.*
import app.domain.tabNavigator.*
import app.domain.uiEvent.doctor.*
import app.domain.uiState.doctor.*
import app.domain.util.args.*
import app.presentation.components.common.*
import kotlinx.coroutines.*

@Composable
fun DoctorsScreen(
    navController: NavController,
    uiState: DoctorsUiState,
    onUiEvent: (DoctorsUiEvent) -> Unit,
    appArgs: AppArgs,
    forResult: Boolean
) {
    val coroutineScope = rememberCoroutineScope()

    val doctorData = remember(uiState.doctorSearchData) { uiState.doctorSearchData.toMutableStateList() }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = if (forResult) "Choose doctor for appointment..." else "Find worker",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            SearchTextField(
                startValue = uiState.searchText,
                onValueChange = { onUiEvent(DoctorsUiEvent.UpdateSearchText(it)) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Search results: ${doctorData.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Sort by",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                onUiEvent(DoctorsUiEvent.Sort(DoctorsSort.NAME))
                            }
                        )
                        .padding(8.dp),
                    text = "Name",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = if (uiState.sort == DoctorsSort.NAME) TextDecoration.Underline else TextDecoration.None
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                onUiEvent(DoctorsUiEvent.Sort(DoctorsSort.AGE))
                            }
                        )
                        .padding(8.dp),
                    text = "Age",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = if (uiState.sort == DoctorsSort.AGE) TextDecoration.Underline else TextDecoration.None
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                onUiEvent(DoctorsUiEvent.Sort(DoctorsSort.POSITION))
                            }
                        )
                        .padding(8.dp),
                    text = "Position",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = if (uiState.sort == DoctorsSort.POSITION) TextDecoration.Underline else TextDecoration.None
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                onUiEvent(DoctorsUiEvent.Sort(DoctorsSort.SALARY))
                            }
                        )
                        .padding(8.dp),
                    text = "Salary",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = if (uiState.sort == DoctorsSort.SALARY) TextDecoration.Underline else TextDecoration.None
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = MaterialTheme.colorScheme.onBackground,
                thickness = 1.dp
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(doctorData) { index, data ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable(
                                onClick = {
                                    if (forResult) {
                                        navController.goBackWith(data.userWorkerId)
                                    } else {
                                        navController.navigate("/info/worker/${data.userWorkerId}")
                                    }
                                }
                            ),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "${index + 1}. ${data.name} ${data.surname}, ${data.age}, \"${data.position}\" for ${data.salary}$",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            if (data.login.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "@${data.login}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            }
                        }
                        Row {
                            Text(
                                text = data.phone,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        thickness = 1.dp
                    )
                }
            }
        }
        if (appArgs == AppArgs.ADMIN) {
            Icon(
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.TopEnd)
                    .clickable(onClick = {
                        coroutineScope.launch {
                            val userDoctorId = navController.navigateForResult("/registration_worker/true")

                            if (userDoctorId != null) {
                                onUiEvent(DoctorsUiEvent.Search(true))
                            }
                        }
                    })
                    .padding(8.dp),
                imageVector = Icons.Default.PersonAdd,
                contentDescription = "Register worker",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}