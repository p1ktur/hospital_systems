package app_client.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import app_client.domain.model.*
import app_client.domain.uiEvent.*
import app_client.domain.uiState.*
import app_shared.presentation.components.*
import moe.tlaster.precompose.navigation.*

@Composable
fun FindClientScreen(
    navigator: Navigator,
    uiState: FindClientUiState,
    onUiEvent: (FindClientUiEvent) -> Unit
) {
    var sort by remember {
        mutableStateOf(FindClientSort.NAME)
    }

    val clientData = remember(uiState.clientSearchData) {
        uiState.clientSearchData.toMutableStateList()
    }

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
            text = "Find patient",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        SearchTextField(
            startValue = uiState.searchText,
            onValueChange = { onUiEvent(FindClientUiEvent.UpdateSearchText(it)) }
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
                            sort = FindClientSort.NAME
                            onUiEvent(FindClientUiEvent.Sort(sort))
                        }
                    )
                    .padding(8.dp),
                text = "Name",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textDecoration = if (sort == FindClientSort.NAME) TextDecoration.Underline else TextDecoration.None
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier
                    .clickable(
                        onClick = {
                            sort = FindClientSort.AGE
                            onUiEvent(FindClientUiEvent.Sort(sort))
                        }
                    )
                    .padding(8.dp),
                text = "Age",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textDecoration = if (sort == FindClientSort.AGE) TextDecoration.Underline else TextDecoration.None
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
                                navigator.navigate("/info/patient/${data.userClientId}")
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
}