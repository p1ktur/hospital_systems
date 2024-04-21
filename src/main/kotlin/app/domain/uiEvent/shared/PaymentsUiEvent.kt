package app.domain.uiEvent.shared

sealed class PaymentsUiEvent {
    data object FetchPaymentsForDoctorOrAdmin : PaymentsUiEvent()
    data class FetchPaymentsForClient(val userClientId: Int) : PaymentsUiEvent()

    data class StartCreatingSubPayment(val userClientId: Int) : PaymentsUiEvent()
    data class CreateSubPayment(val subject: String, val amount: Float) : PaymentsUiEvent()
    data class PayForSubPayment(val userClientId: Int, val subPaymentId: Int, val payedAmount: Float, val payedAccount: String) : PaymentsUiEvent()
    data class DeleteSubPayment(val subPaymentId: Int) : PaymentsUiEvent()
    data class ToggleEditMode(val subPaymentId: Int, val subject: String, val amount: Float) : PaymentsUiEvent()
    data object DisableEditMode : PaymentsUiEvent()

    data object ShowInfoDialog : PaymentsUiEvent()
    data object HideInfoDialog : PaymentsUiEvent()

    data class UpdateDisplayMode(val mode: Int) : PaymentsUiEvent()
    data class UpdatePageTitle(val title: String) : PaymentsUiEvent()
}