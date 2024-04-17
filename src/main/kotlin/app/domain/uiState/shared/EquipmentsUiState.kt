package app.domain.uiState.shared

import app.domain.model.shared.equipment.*
import app.domain.util.editing.*

data class EquipmentsUiState(
    val sort: EquipmentsSort = EquipmentsSort.NAME,
    val searchText: String = "",
    val equipmentSearchData: List<EquipmentSearchData> = emptyList(),
    val errorCodes: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val editState: ItemEditState = ItemEditState.None
)