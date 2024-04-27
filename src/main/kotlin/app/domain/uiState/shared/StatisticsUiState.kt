package app.domain.uiState.shared

import app.domain.model.shared.statistics.*

data class StatisticsUiState(
    val displayMode: Int = 0, // 0 - activity, 1 - expenses, 2 - income, 3 - other
    val isLoading: Boolean = false,
    val errorCodes: List<Int> = emptyList(),
    val appointmentStatistics: AppointmentStatistics = AppointmentStatistics(),
    val hospitalizationStatistics: HospitalizationStatistics = HospitalizationStatistics(),
    val additionalPaymentStatistics: AdditionalPaymentStatistics = AdditionalPaymentStatistics(),
    val salaryStatistics: SalaryStatistics = SalaryStatistics(),
    val totalMoneyStatistics: TotalMoneyStatistics = TotalMoneyStatistics(),
    val registrationStatistics: RegistrationStatistics = RegistrationStatistics(),
    val bestDoctorsByAppointments: List<DoctorForStatistics> = emptyList(),
    val roomDataForStatistics: RoomDataForStatistics = RoomDataForStatistics(),
    val totalWorkers: Int = 0,
    val totalPatients: Int = 0,
    val sicknessStatistics: SicknessStatistics = SicknessStatistics()
)
