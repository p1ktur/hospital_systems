package app.domain.uiEvent.shared

import app.domain.model.shared.room.*

sealed class RoomsUiEvent {
    data class UpdateSearchText(val text: String) : RoomsUiEvent()
    data class Search(val all: Boolean) : RoomsUiEvent()
    data class Sort(val sort: RoomsSort) : RoomsUiEvent()

    data object StartCreating : RoomsUiEvent()
    data class CreateRoom(val room: RoomSearchData) : RoomsUiEvent()
    data object CancelCreating : RoomsUiEvent()
    data class EditRoom(val index: Int) : RoomsUiEvent()
    data object CancelEditing : RoomsUiEvent()
    data class UpdateRoom(val index: Int, val room: RoomSearchData) : RoomsUiEvent()
    data class DeleteRoom(val index: Int, val room: RoomSearchData) : RoomsUiEvent()
}