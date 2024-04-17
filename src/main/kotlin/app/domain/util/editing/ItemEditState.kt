package app.domain.util.editing

sealed class ItemEditState {
    data object None : ItemEditState()
    data object Creating : ItemEditState()
    data class Editing(val index: Int) : ItemEditState()
}