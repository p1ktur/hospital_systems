package app.domain.uiEvent.shared

import app.domain.model.shared.equipment.*

sealed class EquipmentsUiEvent {
    data class UpdateSearchText(val text: String) : EquipmentsUiEvent()
    data object Search : EquipmentsUiEvent()
    data class Sort(val sort: EquipmentsSort) : EquipmentsUiEvent()

    data class StartCreating(val roomId: Int) : EquipmentsUiEvent()
    data class CreateEquipment(val equipment: EquipmentSearchData) : EquipmentsUiEvent()
    data object CancelCreating : EquipmentsUiEvent()
    data class EditEquipment(val index: Int) : EquipmentsUiEvent()
    data object CancelEditing : EquipmentsUiEvent()
    data class UpdateEquipment(val index: Int, val equipment: EquipmentSearchData) : EquipmentsUiEvent()
    data class DeleteEquipment(val index: Int, val equipment: EquipmentSearchData) : EquipmentsUiEvent()
}