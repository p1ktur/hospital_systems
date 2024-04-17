package app.domain.model.shared.hospitalization

import app.domain.model.shared.payment.*

data class FetchHospitalizationData(
    val hospitalizations: List<Hospitalization> = emptyList(),
    val payments: List<Payment> = emptyList()
)