package app.domain.uiEvent.shared

sealed class HospitalizationsUiEvent {
    data class FetchHospitalizationsForDoctorOrAdmin(val openId: Int?) : HospitalizationsUiEvent()
    data class FetchHospitalizationsForClient(val userClientId: Int, val openId: Int?) : HospitalizationsUiEvent()

    data class StartCreatingHospitalization(val userClientId: Int, val roomId: Int) : HospitalizationsUiEvent()
    data class CreateHospitalization(val reason: String, val price: Float) : HospitalizationsUiEvent()
    data class PayForHospitalization(val userClientId: Int, val hospitalizationId: Int, val payedAmount: Float, val payedAccount: String) : HospitalizationsUiEvent()
    data class EndHospitalization(val hospitalizationId: Int) : HospitalizationsUiEvent()
    data class DeleteHospitalization(val hospitalizationId: Int) : HospitalizationsUiEvent()
    data class ToggleEditMode(val hospitalizationId: Int, val reason: String, val price: Float) : HospitalizationsUiEvent()
    data object DisableEditMode : HospitalizationsUiEvent()

    data object ShowInfoDialog : HospitalizationsUiEvent()
    data object HideInfoDialog : HospitalizationsUiEvent()
}