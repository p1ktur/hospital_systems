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
import app.domain.model.shared.equipment.*
import app.domain.tabNavigator.*
import app.domain.uiEvent.shared.*
import app.domain.uiState.shared.*
import app.domain.util.editing.*
import app.presentation.codes.*
import app.presentation.components.common.*
import kotlinx.coroutines.*

@Composable
fun EquipmentsScreen(
    navController: NavController,
    uiState: EquipmentsUiState,
    onUiEvent: (EquipmentsUiEvent) -> Unit,
    forResult: Boolean
) {
    val coroutineScope = rememberCoroutineScope()

    val equipmentsData = remember(uiState.equipmentSearchData) { uiState.equipmentSearchData.toMutableStateList() }

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
                text = if (forResult) "Choose equipment" else "Find equipment",
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
                onValueChange = { onUiEvent(EquipmentsUiEvent.UpdateSearchText(it)) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Search results: ${equipmentsData.size}",
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
                                onUiEvent(EquipmentsUiEvent.Sort(EquipmentsSort.NAME))
                            }
                        )
                        .padding(8.dp),
                    text = "Name",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = if (uiState.sort == EquipmentsSort.NAME) TextDecoration.Underline else TextDecoration.None
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
                itemsIndexed(equipmentsData) { index, equipment ->
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
                            var notesText by remember { mutableStateOf("") }

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
                                    startValue = notesText,
                                    label = "Notes:",
                                    maxLength = 255,
                                    onValueChange = { notesText = it }
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                if (nameText.isNotBlank()) {
                                    Icon(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clickable(onClick = {
                                                onUiEvent(
                                                    EquipmentsUiEvent.CreateEquipment(equipment.copy(
                                                        name = nameText,
                                                        notes = notesText
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
                            var nameText by remember { mutableStateOf(equipment.name) }
                            var notesText by remember { mutableStateOf(equipment.notes) }

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
                                    startValue = notesText,
                                    label = "Notes:",
                                    maxLength = 255,
                                    onValueChange = { notesText = it }
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                if (nameText.isNotBlank()) {
                                    Icon(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clickable(onClick = {
                                                onUiEvent(
                                                    EquipmentsUiEvent.UpdateEquipment(index, equipment.copy(
                                                        name = nameText,
                                                        notes = notesText
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
                                                navController.goBackWith(equipment.id)
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
                                    text = "${index + 1}. ${equipment.name}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (equipment.notes.isNotBlank()) {
                                    ReducedText(
                                        text = "Notes: ${equipment.notes}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                if (equipment.room != null) {
                                    Text(
                                        text = "Room: ${equipment.room.name}, ${equipment.room.number}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                Row {
                                    Icon(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clickable(onClick = {
                                                onUiEvent(EquipmentsUiEvent.EditEquipment(index))
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
                                                onUiEvent(EquipmentsUiEvent.DeleteEquipment(index, equipment))
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
                                onUiEvent(EquipmentsUiEvent.CancelCreating)
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
                                coroutineScope.launch {
                                    val roomId = navController.navigateForResult("/find_room/true/true") as? Int

                                    if (roomId != null) {
                                        onUiEvent(EquipmentsUiEvent.StartCreating(roomId))
                                    }
                                }
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