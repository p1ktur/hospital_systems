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
import app.domain.model.shared.room.*
import app.domain.tabNavigator.*
import app.domain.uiEvent.shared.*
import app.domain.uiState.shared.*
import app.domain.util.editing.*
import app.domain.util.numbers.*
import app.presentation.codes.*
import app.presentation.components.common.*

@Composable
fun RoomsScreen(
    navController: NavController,
    uiState: RoomsUiState,
    onUiEvent: (RoomsUiEvent) -> Unit,
    forResult: Boolean
) {
    val roomsData = remember(uiState.roomSearchData) { uiState.roomSearchData.toMutableStateList() }

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
                text = if (forResult) "Choose room" else "Find room",
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
                onValueChange = { onUiEvent(RoomsUiEvent.UpdateSearchText(it)) }
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
                                onUiEvent(RoomsUiEvent.Sort(RoomsSort.NAME))
                            }
                        )
                        .padding(8.dp),
                    text = "Name",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = if (uiState.sort == RoomsSort.NAME) TextDecoration.Underline else TextDecoration.None
                )
                if (uiState.preloadedTypes.size > 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    onUiEvent(RoomsUiEvent.Sort(RoomsSort.TYPE))
                                }
                            )
                            .padding(8.dp),
                        text = "Type",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textDecoration = if (uiState.sort == RoomsSort.TYPE) TextDecoration.Underline else TextDecoration.None
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                onUiEvent(RoomsUiEvent.Sort(RoomsSort.FLOOR))
                            }
                        )
                        .padding(8.dp),
                    text = "Floor",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = if (uiState.sort == RoomsSort.FLOOR) TextDecoration.Underline else TextDecoration.None
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                onUiEvent(RoomsUiEvent.Sort(RoomsSort.NUMBER))
                            }
                        )
                        .padding(8.dp),
                    text = "Number",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = if (uiState.sort == RoomsSort.NUMBER) TextDecoration.Underline else TextDecoration.None
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(roomsData) { index, room ->
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
                            var floorText by remember { mutableStateOf("1") }
                            var numberText by remember { mutableStateOf("101") }
                            var typeIndex by remember { mutableIntStateOf(0) }

                            Column {
                                DefaultTextField(
                                    startValue = nameText,
                                    label = "Name:",
                                    onValueChange = { nameText = it }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                DefaultTextField(
                                    startValue = floorText,
                                    label = "Floor:",
                                    onValueChange = { floorText = it },
                                    onlyIntegerNumbers = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                DefaultTextField(
                                    startValue = numberText,
                                    label = "Number:",
                                    onValueChange = { numberText = it }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OptionsTextField(
                                    startValue = uiState.preloadedTypes.getOrNull(typeIndex)?.second.toString(),
                                    label = "Type:",
                                    options = uiState.preloadedTypes.map { it.second },
                                    onOptionSelected = { typeIndex = it }
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = room.type,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Icon(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clickable(onClick = {
                                            onUiEvent(
                                                RoomsUiEvent.CreateRoom(room.copy(
                                                name = nameText,
                                                floor = floorText.toInt(),
                                                number = numberText.toInt(),
                                                type = uiState.preloadedTypes[typeIndex].second
                                            )))
                                        })
                                        .padding(8.dp),
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "Save changes",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    } else if (isEdited && !forResult) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {
                                        navController.goBackWith(room.id)
                                    }
                                ),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            var nameText by remember { mutableStateOf(room.name) }
                            var floorText by remember { mutableStateOf(room.floor.toString()) }
                            var numberText by remember { mutableStateOf(room.number.toString()) }
                            var typeIndex by remember { mutableIntStateOf(uiState.preloadedTypes.map { it.second }.indexOf(room.type)) }

                            Column {
                                DefaultTextField(
                                    startValue = nameText,
                                    label = "Name:",
                                    onValueChange = { nameText = it }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                DefaultTextField(
                                    startValue = floorText,
                                    label = "Floor:",
                                    onValueChange = { floorText = it },
                                    onlyIntegerNumbers = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                DefaultTextField(
                                    startValue = numberText,
                                    label = "Number:",
                                    onValueChange = { numberText = it }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OptionsTextField(
                                    startValue = uiState.preloadedTypes.getOrNull(typeIndex)?.second.toString(),
                                    label = "Type:",
                                    options = uiState.preloadedTypes.map { it.second },
                                    onOptionSelected = { typeIndex = it }
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = room.type,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Icon(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clickable(onClick = {
                                            onUiEvent(
                                                RoomsUiEvent.UpdateRoom(index, room.copy(
                                                name = nameText,
                                                floor = floorText.toInt(),
                                                number = numberText.toInt(),
                                                type = uiState.preloadedTypes[typeIndex].second
                                            )))
                                        })
                                        .padding(8.dp),
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "Save changes",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
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
                                                navController.goBackWith(room.id)
                                            }
                                        )
                                    } else {
                                        Modifier
                                    }
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${index + 1}. ${room.name} №${room.number} on ${room.floor.asOrdinal()} floor",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = room.type,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row {
                                    Icon(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clickable(onClick = {
                                                onUiEvent(RoomsUiEvent.EditRoom(index))
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
                                                onUiEvent(RoomsUiEvent.DeleteRoom(index, room))
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
                                onUiEvent(RoomsUiEvent.CancelCreating)
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
                                onUiEvent(RoomsUiEvent.StartCreating)
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