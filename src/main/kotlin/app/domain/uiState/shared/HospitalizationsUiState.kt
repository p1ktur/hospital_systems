package app.domain.uiState.shared

import app.domain.model.shared.hospitalization.*
import app.domain.model.shared.payment.*

data class HospitalizationsUiState(
    val editMode: Boolean = false,
    val hospitalizations: List<Hospitalization> = emptyList(),
    val payments: List<Payment.Default> = emptyList(),
    val isLoading: Boolean = false,
    val errorCodes: List<Int> = emptyList(),
    val creatingHospitalization: Boolean = false,
    val showInfoDialog: Boolean = false,
    val showDateTimePickerDialog: Boolean = false,
    val userClientIdForHospitalization: Int? = null,
    val roomIdForHospitalization: Int? = null,
    val openId: Int? = null
)
