package app_shared.domain.uiState

import app_shared.domain.model.forShared.*
import app_shared.domain.model.forShared.hospitalization.*

data class HospitalizationsUiState(
    val editMode: Boolean = false,
    val hospitalizations: List<Hospitalization> = emptyList(),
    val payments: List<Payment> = emptyList(),
    val isLoading: Boolean = false,
    val errorCodes: List<Int> = emptyList(),
    val creatingHospitalization: Boolean = false,
    val showInfoDialog: Boolean = false,
    val showDateTimePickerDialog: Boolean = false,
    val userClientIdForHospitalization: Int? = null,
    val roomIdForHospitalization: Int? = null,
)
