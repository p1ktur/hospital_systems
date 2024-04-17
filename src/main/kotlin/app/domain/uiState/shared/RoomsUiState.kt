package app.domain.uiState.shared

import app.domain.model.shared.room.*
import app.domain.util.editing.*

data class RoomsUiState(
    val sort: RoomsSort = RoomsSort.NAME,
    val searchText: String = "",
    val roomSearchData: List<RoomSearchData> = emptyList(),
    val errorCodes: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val editState: ItemEditState = ItemEditState.None,
    val preloadedTypes: List<Pair<Int, String>> = emptyList()
)