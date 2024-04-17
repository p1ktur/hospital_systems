package app.domain.viewModel.shared

import app.data.shared.*
import app.domain.database.transactor.*
import app.domain.model.shared.drug.*
import app.domain.uiEvent.shared.*
import app.domain.uiState.shared.*
import app.domain.util.editing.*
import app.domain.util.exceptions.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class DrugsViewModel(private val drugsRepository: DrugsRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<DrugsUiState> = MutableStateFlow(DrugsUiState())
    val uiState = _uiState.asStateFlow()

    private var fetchedDrugData: List<Drug> = emptyList()
    private var sortAscending = true

    fun onUiEvent(event: DrugsUiEvent) {
        when (event) {
            is DrugsUiEvent.UpdateSearchText -> updateSearchText(event.text)
            is DrugsUiEvent.Search -> search()
            is DrugsUiEvent.Sort -> sort(event.sort)

            DrugsUiEvent.StartCreating -> startCreating()
            is DrugsUiEvent.CreateDrug -> create(event.drug)
            DrugsUiEvent.CancelCreating -> cancelCreating()
            is DrugsUiEvent.EditDrug -> edit(event.index)
            DrugsUiEvent.CancelEditing -> cancelEditing()
            is DrugsUiEvent.UpdateDrug -> update(event.index, event.drug)
            is DrugsUiEvent.DeleteDrug -> delete(event.index, event.drug)
        }
    }

    private fun updateSearchText(text: String) {
        _uiState.value = uiState.value.copy(
            searchText = text,
            drugSearchData = fetchedDrugData.filter {
                (it.name + it.amount).contains(text, ignoreCase = true)
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

            when (val searchResult = drugsRepository.search()) {
                is TransactorResult.Failure -> Unit
                is TransactorResult.Success<*> -> {
                    fetchedDrugData = searchResult.data as List<Drug>

                    _uiState.value = uiState.value.copy(
                        drugSearchData = fetchedDrugData,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun sort(sort: DrugsSort) {
        if (_uiState.value.editState != ItemEditState.None || uiState.value.isLoading) return

        if (sort == uiState.value.sort) {
            sortAscending = !sortAscending
        } else {
            _uiState.value = uiState.value.copy(
                sort = sort
            )
        }

        _uiState.value = uiState.value.copy(
            drugSearchData = if (sortAscending) {
                when (sort) {
                    DrugsSort.NAME -> uiState.value.drugSearchData.sortedBy { it.name }
                    DrugsSort.AMOUNT -> uiState.value.drugSearchData.sortedBy { it.amount }
                }
            } else {
                when (sort) {
                    DrugsSort.NAME -> uiState.value.drugSearchData.sortedByDescending { it.name }
                    DrugsSort.AMOUNT -> uiState.value.drugSearchData.sortedByDescending { it.amount }
                }
            }
        )
    }

    private fun startCreating() {
        if (_uiState.value.editState != ItemEditState.None || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.Creating,
            drugSearchData = listOf(Drug(-1, "", "", "", 0)) + uiState.value.drugSearchData
        )
    }

    private fun create(drug: Drug) {
        if (_uiState.value.editState != ItemEditState.Creating || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.None,
            drugSearchData = uiState.value.drugSearchData.toMutableList().apply {
                set(0, drug)
            }
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )
            when (val createResult = drugsRepository.create(drug)) {
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
            drugSearchData = uiState.value.drugSearchData.drop(1)
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

    private fun update(index: Int, drug: Drug) {
        if (_uiState.value.editState !is ItemEditState.Editing || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.None,
            drugSearchData = uiState.value.drugSearchData.toMutableList().apply {
                set(index, drug)
            }
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )
            when (val updateResult = drugsRepository.update(drug)) {
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

    private fun delete(index: Int, drug: Drug) {
        if (_uiState.value.editState != ItemEditState.None || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.None,
            drugSearchData = uiState.value.drugSearchData.toMutableList().apply {
                removeAt(index)
            }
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )
            when (val deleteResult = drugsRepository.delete(drug)) {
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