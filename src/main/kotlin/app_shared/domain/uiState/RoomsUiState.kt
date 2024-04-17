package app_shared.domain.uiState

import app_shared.domain.model.forShared.room.*
import app_shared.domain.model.util.editing.*

data class RoomsUiState(
    val sort: RoomsSort = RoomsSort.NAME,
    val searchText: String = "",
    val roomSearchData: List<RoomSearchData> = emptyList(),
    val errorCodes: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val editState: ItemEditState = ItemEditState.None,
    val preloadedTypes: List<Pair<Int, String>> = emptyList()
)