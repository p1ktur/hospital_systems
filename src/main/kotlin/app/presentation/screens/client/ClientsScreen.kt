package app.presentation.screens.client

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
import app.domain.model.client.*
import app.domain.tabNavigator.*
import app.domain.uiEvent.client.*
import app.domain.uiState.client.*
import app.domain.util.args.*
import app.presentation.components.common.*
import kotlinx.coroutines.*

@Composable
fun ClientsScreen(
    navController: NavController,
    uiState: ClientsUiState,
    onUiEvent: (ClientsUiEvent) -> Unit,
    appArgs: AppArgs,
    forResult: Boolean
) {
    val coroutineScope = rememberCoroutineScope()

    val clientData = remember(uiState.clientSearchData) { uiState.clientSearchData.toMutableStateList() }

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
                text = if (forResult) "Create appointment with... " else "Find patient",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            SearchTextField(
                startValue = uiState.searchText,
                onValueChange = { onUiEvent(ClientsUiEvent.UpdateSearchText(it)) }
            )
            Spacer(modifier = Modifier.height(16.dp))
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
                                onUiEvent(ClientsUiEvent.Sort(ClientsSort.NAME))
                            }
                        )
                        .padding(8.dp),
                    text = "Name",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = if (uiState.sort == ClientsSort.NAME) TextDecoration.Underline else TextDecoration.None
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                onUiEvent(ClientsUiEvent.Sort(ClientsSort.AGE))
                            }
                        )
                        .padding(8.dp),
                    text = "Age",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = if (uiState.sort == ClientsSort.AGE) TextDecoration.Underline else TextDecoration.None
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(clientData) { index, data ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable(
                                onClick = {
                                    if (forResult) {
                                        navController.goBackWith(data.userClientId)
                                    } else {
                                        navController.navigate("/info/patient/${data.userClientId}")
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
                                text = "${index + 1}. ${data.name} ${data.surname}, ${data.age}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "@${data.login}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
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
        if (appArgs != AppArgs.CLIENT) {
            Icon(
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.TopEnd)
                    .clickable(onClick = {
                        coroutineScope.launch {
                            val userClientId = navController.navigateForResult("/registration_patient/true")

                            if (userClientId != null) {
                                onUiEvent(ClientsUiEvent.Search)
                            }
                        }
                    })
                    .padding(8.dp),
                imageVector = Icons.Default.PersonAdd,
                contentDescription = "Register patient",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}