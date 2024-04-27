package app.presentation.screens.shared

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
import app.domain.model.shared.drug.*
import app.domain.tabNavigator.*
import app.domain.uiEvent.shared.*
import app.domain.uiState.shared.*
import app.domain.util.editing.*
import app.presentation.codes.*
import app.presentation.components.common.*

@Composable
fun DrugsScreen(
    navController: NavController,
    uiState: DrugsUiState,
    onUiEvent: (DrugsUiEvent) -> Unit,
    forResult: Boolean
) {
    val drugsData = remember(uiState.drugSearchData) { uiState.drugSearchData.toMutableStateList() }

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
                text = if (forResult) "Choose drug" else "Find drug",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            if (uiState.errorCodes.contains(1101)) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = parseDefaultErrorCode(1101),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }
            SearchTextField(
                startValue = uiState.searchText,
                onValueChange = { onUiEvent(DrugsUiEvent.UpdateSearchText(it)) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Search results: ${drugsData.size}",
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
                                onUiEvent(DrugsUiEvent.Sort(DrugsSort.NAME))
                            }
                        )
                        .padding(8.dp),
                    text = "Name",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = if (uiState.sort == DrugsSort.NAME) TextDecoration.Underline else TextDecoration.None
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                onUiEvent(DrugsUiEvent.Sort(DrugsSort.AMOUNT))
                            }
                        )
                        .padding(8.dp),
                    text = "Amount",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = if (uiState.sort == DrugsSort.AMOUNT) TextDecoration.Underline else TextDecoration.None
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
                itemsIndexed(drugsData) { index, drug ->
                    val isEdited by remember(uiState.editState) {
                        mutableStateOf(uiState.editState is ItemEditState.Editing && uiState.editState.index == index)
                    }

                    val isCreated by remember(uiState.editState) {
                        mutableStateOf(uiState.editState == ItemEditState.Creating && index == 0)
                    }

                    if (isCreated && !forResult) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            var nameText by remember { mutableStateOf("") }
                            var appliancesText by remember { mutableStateOf("") }
                            var notesText by remember { mutableStateOf("") }
                            var amountText by remember { mutableStateOf("0") }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                DefaultTextField(
                                    startValue = nameText,
                                    label = "Name:",
                                    onValueChange = { nameText = it }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                DefaultTextField(
                                    startValue = appliancesText,
                                    label = "Appliances:",
                                    maxLength = 255,
                                    onValueChange = { appliancesText = it }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                DefaultTextField(
                                    startValue = notesText,
                                    label = "Notes:",
                                    maxLength = 255,
                                    onValueChange = { notesText = it }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                DefaultTextField(
                                    startValue = amountText,
                                    label = "Amount:",
                                    onValueChange = { amountText = it },
                                    onlyIntegerNumbers = true
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                if (amountText.isNotBlank() && nameText.isNotBlank()) {
                                    Icon(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clickable(onClick = {
                                                onUiEvent(
                                                    DrugsUiEvent.CreateDrug(drug.copy(
                                                        name = nameText,
                                                        appliances = appliancesText,
                                                        notes = notesText,
                                                        amount = amountText.toInt()
                                                    ))
                                                )
                                            })
                                            .padding(8.dp),
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "Save changes",
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    } else if (isEdited && !forResult) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            var nameText by remember { mutableStateOf(drug.name) }
                            var appliancesText by remember { mutableStateOf(drug.appliances) }
                            var notesText by remember { mutableStateOf(drug.notes) }
                            var amountText by remember { mutableStateOf(drug.amount.toString()) }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                DefaultTextField(
                                    startValue = nameText,
                                    label = "Name:",
                                    onValueChange = { nameText = it }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                DefaultTextField(
                                    startValue = appliancesText,
                                    label = "Appliances:",
                                    maxLength = 255,
                                    onValueChange = { appliancesText = it }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                DefaultTextField(
                                    startValue = notesText,
                                    label = "Notes:",
                                    maxLength = 255,
                                    onValueChange = { notesText = it }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                DefaultTextField(
                                    startValue = amountText,
                                    label = "Amount:",
                                    onValueChange = { amountText = it },
                                    onlyIntegerNumbers = true
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                if (amountText.isNotBlank() && nameText.isNotBlank()) {
                                    Icon(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clickable(onClick = {
                                                onUiEvent(
                                                    DrugsUiEvent.UpdateDrug(index, drug.copy(
                                                        name = nameText,
                                                        appliances = appliancesText,
                                                        notes = notesText,
                                                        amount = amountText.toInt()
                                                    ))
                                                )
                                            })
                                            .padding(8.dp),
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "Save changes",
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (forResult) {
                                        Modifier.clickable(
                                            onClick = {
                                                navController.goBackWith(drug.id)
                                            }
                                        )
                                    } else {
                                        Modifier
                                    }
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${index + 1}. ${drug.name}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (drug.appliances.isNotBlank()) {
                                    ReducedText(
                                        text = "Appliances: ${drug.appliances}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                if (drug.notes.isNotBlank()) {
                                    ReducedText(
                                        text = "Notes: ${drug.notes}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "Amount: ${drug.amount}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row {
                                    Icon(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clickable(onClick = {
                                                onUiEvent(DrugsUiEvent.EditDrug(index))
                                            })
                                            .padding(8.dp),
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit room",
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clickable(onClick = {
                                                onUiEvent(DrugsUiEvent.DeleteDrug(index, drug))
                                            })
                                            .padding(8.dp),
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete room",
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
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
        if (!forResult) {
            when (uiState.editState) {
                ItemEditState.Creating -> {
                    Icon(
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.TopEnd)
                            .clickable(onClick = {
                                onUiEvent(DrugsUiEvent.CancelCreating)
                            })
                            .padding(8.dp),
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Cancel creating",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                is ItemEditState.Editing -> Unit
                ItemEditState.None -> {
                    Icon(
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.TopEnd)
                            .clickable(onClick = {
                                onUiEvent(DrugsUiEvent.StartCreating)
                            })
                            .padding(8.dp),
                        imageVector = Icons.Default.Create,
                        contentDescription = "Cancel editing",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}