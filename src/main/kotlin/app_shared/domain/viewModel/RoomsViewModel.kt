package app_shared.domain.viewModel

import app_shared.data.*
import app_shared.domain.model.database.transactor.*
import app_shared.domain.model.forShared.room.*
import app_shared.domain.model.util.editing.*
import app_shared.domain.model.util.exceptions.*
import app_shared.domain.uiEvent.*
import app_shared.domain.uiState.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class RoomsViewModel(private val roomsRepository: RoomsRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<RoomsUiState> = MutableStateFlow(RoomsUiState())
    val uiState = _uiState.asStateFlow()

    private var fetchedRoomData: List<RoomSearchData> = emptyList()

    fun onUiEvent(event: RoomsUiEvent) {
        when (event) {
            is RoomsUiEvent.UpdateSearchText -> updateSearchText(event.text)
            is RoomsUiEvent.Search -> search(event.all)
            is RoomsUiEvent.Sort -> sort(event.sort)

            RoomsUiEvent.StartCreating -> startCreating()
            is RoomsUiEvent.CreateRoom -> create(event.room)
            RoomsUiEvent.CancelCreating -> cancelCreating()
            is RoomsUiEvent.EditRoom -> edit(event.index)
            RoomsUiEvent.CancelEditing -> cancelEditing()
            is RoomsUiEvent.UpdateRoom -> update(event.index, event.room)
            is RoomsUiEvent.DeleteRoom -> delete(event.index, event.room)
        }
    }

    private fun updateSearchText(text: String) {
        _uiState.value = uiState.value.copy(
            searchText = text,
            roomSearchData = fetchedRoomData.filter {
                (it.name + it.type + it.floor + it.number).contains(text, ignoreCase = true)
            }
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun search(all: Boolean) {
        if (uiState.value.isLoading) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )

            when (val searchResult = roomsRepository.search(all)) {
                is TransactorResult.Failure -> Unit
                is TransactorResult.Success<*> -> {
                    fetchedRoomData = searchResult.data as List<RoomSearchData>

                    _uiState.value = uiState.value.copy(
                        roomSearchData = fetchedRoomData,
                        isLoading = false
                    )

                    when (val typesResult = roomsRepository.preloadTypes()) {
                        is TransactorResult.Failure -> Unit
                        is TransactorResult.Success<*> -> {
                            val preloadedTypes = typesResult.data as List<Pair<Int, String>>

                            _uiState.value = uiState.value.copy(
                                preloadedTypes = preloadedTypes,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun sort(sort: RoomsSort) {
        if (_uiState.value.editState != ItemEditState.None || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            sort = sort,
            roomSearchData = when (sort) {
                RoomsSort.NAME -> uiState.value.roomSearchData.sortedBy { it.name }
                RoomsSort.TYPE -> uiState.value.roomSearchData.sortedBy { it.type }
                RoomsSort.FLOOR -> uiState.value.roomSearchData.sortedBy { it.floor }
                RoomsSort.NUMBER -> uiState.value.roomSearchData.sortedBy { it.number }
            }
        )
    }

    private fun startCreating() {
        if (_uiState.value.editState != ItemEditState.None || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.Creating,
            roomSearchData = listOf(RoomSearchData(-1, "", "", 0, 0)) + uiState.value.roomSearchData
        )
    }

    private fun create(room: RoomSearchData) {
        if (_uiState.value.editState != ItemEditState.Creating || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.None,
            roomSearchData = uiState.value.roomSearchData.toMutableList().apply {
                set(0, room)
            }
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )
            when (val createResult = roomsRepository.create(room)) {
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
            roomSearchData = fetchedRoomData.drop(1)
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

    private fun update(index: Int, room: RoomSearchData) {
        if (_uiState.value.editState !is ItemEditState.Editing || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.None,
            roomSearchData = uiState.value.roomSearchData.toMutableList().apply {
                set(index, room)
            }
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )
            when (val updateResult = roomsRepository.update(room)) {
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

    private fun delete(index: Int, room: RoomSearchData) {
        if (_uiState.value.editState != ItemEditState.None || uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            editState = ItemEditState.None,
            roomSearchData = uiState.value.roomSearchData.toMutableList().apply {
                removeAt(index)
            }
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )
            when (val deleteResult = roomsRepository.delete(room)) {
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