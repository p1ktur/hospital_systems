package app.domain.viewModel.shared

import app.data.shared.*
import app.domain.database.transactor.*
import app.domain.model.shared.drug.*
import app.domain.model.shared.equipment.*
import app.domain.uiEvent.shared.*
import app.domain.uiState.shared.*
import app.domain.util.editing.*
import app.domain.util.exceptions.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class EquipmentsViewModel(private val equipmentsRepository: EquipmentsRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<EquipmentsUiState> = MutableStateFlow(EquipmentsUiState())
    val uiState = _uiState.asStateFlow()

    private var fetchedEquipmentData: List<EquipmentSearchData> = emptyList()
    private var sortAscending = true

    fun onUiEvent(event: EquipmentsUiEvent) {
        when (event) {
            is EquipmentsUiEvent.UpdateSearchText -> updateSearchText(event.text)
            is EquipmentsUiEvent.Search -> search()
            is EquipmentsUiEvent.Sort -> sort(event.sort)

            is EquipmentsUiEvent.StartCreating -> startCreating(event.roomId)
            is EquipmentsUiEvent.CreateEquipment -> create(event.equipment)
            EquipmentsUiEvent.CancelCreating -> cancelCreating()
            is EquipmentsUiEvent.EditEquipment -> edit(event.index)
            EquipmentsUiEvent.CancelEditing -> cancelEditing()
            is EquipmentsUiEvent.UpdateEquipment -> update(event.index, event.equipment)
            is EquipmentsUiEvent.DeleteEquipment -> delete(event.index, event.equipment)
        }
    }

    private fun updateSearchText(text: String) {
        _uiState.value = uiState.value.copy(
            searchText = text,
            equipmentSearchData = fetchedEquipmentData.filter {
                it.name.contains(text, ignoreCase = true)
            }
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun search() {
        if (uiState.value.isLoading) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )

            when (val searchResult = equipmentsRepository.search()) {
                is TransactorResult.Failure -> Unit
                is TransactorResult.Success<*> -> {
                    fetchedEquipmentData = searchResult.data as List<EquipmentSearchData>

                    _uiState.value = uiState.value.copy(
                        equipmentSearchData = fetchedEquipmentData,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun sort(sort: EquipmentsSort) {
        if (_uiState.value.editState != ItemEditState.None || uiState.value.isLoading) return

        if (sort == uiState.value.sort) {
            sortAscending = !sortAscending
        } else {
            _uiState.value = uiState.value.copy(
                sort = sort
            )
        }

        _uiState.value = uiState.value.copy(
            equipmentSearchData = if (sortAscending) {
                when (sort) {
                    EquipmentsSort.NAME -> uiState.value.equipmentSearchData.sortedBy { it.name }
                }
            } else {
                when (sort) {
                    EquipmentsSort.NAME -> uiState.value.equipmentSearchData.sortedByDescending { it.name }
                }
            }
        )
    }

    private fun startCreating(roomId: Int) {
        if (_uiState.value.editState != ItemEditState.None || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.Creating,
            equipmentSearchData = listOf(EquipmentSearchData(-1, roomId, "", "", null)) + uiState.value.equipmentSearchData
        )
    }

    private fun create(equipment: EquipmentSearchData) {
        if (_uiState.value.editState != ItemEditState.Creating || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.None,
            equipmentSearchData = uiState.value.equipmentSearchData.toMutableList().apply {
                set(0, equipment)
            }
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )
            when (val createResult = equipmentsRepository.create(equipment)) {
                is TransactorResult.Failure -> {
                    if (createResult.exception is AlreadyExistsException) {
                        _uiState.value = uiState.value.copy(
                            errorCodes = uiState.value.errorCodes + createResult.exception.code,
                            isLoading = false
                        )
                    }
                }
                is TransactorResult.Success<*> -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun cancelCreating() {
        if (_uiState.value.editState != ItemEditState.Creating || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.None,
            equipmentSearchData = uiState.value.equipmentSearchData.drop(1)
        )
    }

    private fun edit(index: Int) {
        if (_uiState.value.editState != ItemEditState.None || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.Editing(index)
        )
    }

    private fun cancelEditing() {
        if (_uiState.value.editState !is ItemEditState.Editing || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.None
        )
    }

    private fun update(index: Int, equipment: EquipmentSearchData) {
        if (_uiState.value.editState !is ItemEditState.Editing || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.None,
            equipmentSearchData = uiState.value.equipmentSearchData.toMutableList().apply {
                set(index, equipment)
            }
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )
            when (val updateResult = equipmentsRepository.update(equipment)) {
                is TransactorResult.Failure -> {
                    if (updateResult.exception is AlreadyExistsException) {
                        _uiState.value = uiState.value.copy(
                            errorCodes = uiState.value.errorCodes + updateResult.exception.code,
                            isLoading = false
                        )
                    }
                }
                is TransactorResult.Success<*> -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun delete(index: Int, equipment: EquipmentSearchData) {
        if (_uiState.value.editState != ItemEditState.None || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.None,
            equipmentSearchData = uiState.value.equipmentSearchData.toMutableList().apply {
                removeAt(index)
            }
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )
            when (val deleteResult = equipmentsRepository.delete(equipment)) {
                is TransactorResult.Failure -> {
                    if (deleteResult.exception is AlreadyExistsException) {
                        _uiState.value = uiState.value.copy(
                            errorCodes = uiState.value.errorCodes + deleteResult.exception.code,
                            isLoading = false
                        )
                    }
                }
                is TransactorResult.Success<*> -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
}