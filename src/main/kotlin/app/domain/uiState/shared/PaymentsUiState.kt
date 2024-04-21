package app.domain.uiState.shared

import app.domain.model.shared.payment.*

data class PaymentsUiState(
    val displayMode: Int = 0, // 0 -> payments; 1 -> subPayments; 2 -> all
    val pageTitle: String = "Additional payments",
    val editMode: Boolean = false,
    val payments: List<Payment.Default> = emptyList(),
    val subPayments: List<Payment.Sub> = emptyList(),
    val isLoading: Boolean = false,
    val errorCodes: List<Int> = emptyList(),
    val creatingSubPayment: Boolean = false,
    val showInfoDialog: Boolean = false,
    val userClientIdForSubPayment: Int? = null
)
