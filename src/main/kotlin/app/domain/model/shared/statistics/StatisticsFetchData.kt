package app.domain.model.shared.statistics

data class StatisticsFetchData(
    val appointmentStatistics: AppointmentStatistics,
    val hospitalizationStatistics: HospitalizationStatistics,
    val additionalPaymentStatistics: AdditionalPaymentStatistics,
    val salaryStatistics: SalaryStatistics,
    val totalMoneyStatistics: TotalMoneyStatistics,
    val registrationStatistics: RegistrationStatistics,
    val bestDoctorsByAppointments: List<DoctorForStatistics>,
    val roomDataForStatistics: RoomDataForStatistics,
    val totalWorkers: Int,
    val totalPatients: Int,
    val sicknessStatistics: SicknessStatistics
)
