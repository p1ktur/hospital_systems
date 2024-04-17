package app_shared.domain.model.forShared.hospitalization

import app_shared.domain.model.forShared.*

data class FetchHospitalizationData(
    val hospitalizations: List<Hospitalization> = emptyList(),
    val payments: List<Payment> = emptyList()
)