package app.domain.uiEvent.shared

import app.domain.model.shared.drug.*

sealed class DrugsUiEvent {
    data class UpdateSearchText(val text: String) : DrugsUiEvent()
    data object Search : DrugsUiEvent()
    data class Sort(val sort: DrugsSort) : DrugsUiEvent()

    data object StartCreating : DrugsUiEvent()
    data class CreateDrug(val drug: Drug) : DrugsUiEvent()
    data object CancelCreating : DrugsUiEvent()
    data class EditDrug(val index: Int) : DrugsUiEvent()
    data object CancelEditing : DrugsUiEvent()
    data class UpdateDrug(val index: Int, val drug: Drug) : DrugsUiEvent()
    data class DeleteDrug(val index: Int, val drug: Drug) : DrugsUiEvent()
}