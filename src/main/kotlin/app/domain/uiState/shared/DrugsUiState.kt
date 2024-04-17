package app.domain.uiState.shared

import app.domain.model.shared.drug.*
import app.domain.util.editing.*

data class DrugsUiState(
    val sort: DrugsSort = DrugsSort.NAME,
    val searchText: String = "",
    val drugSearchData: List<Drug> = emptyList(),
    val errorCodes: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val editState: ItemEditState = ItemEditState.None
)