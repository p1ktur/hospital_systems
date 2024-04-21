package app.data.shared

import app.domain.database.transactor.*
import app.domain.model.shared.statistics.*
import app.domain.util.time.*
import kotlinx.coroutines.*
import java.time.*
import kotlin.math.*

class StatisticsRepository(private val transactor: ITransactor) {

    suspend fun fetchStatisticsData(scope: CoroutineScope): TransactorResult = transactor.startSuspendTransaction {
        var ast: AppointmentStatistics? = null
        var hs: HospitalizationStatistics? = null
        var ap: AdditionalPaymentStatistics? = null
        var sa: SalaryStatistics? = null
        var re: RegistrationStatistics? = null
        var bd: List<DoctorForStatistics> = emptyList()
        var rd: RoomDataForStatistics? = null
        var tw = 0
        var tp = 0

        awaitAll(
            scope.async(Dispatchers.IO) {
                // Appointments
                val asStatement = createStatement()
                val asResult = asStatement.executeQuery("SELECT date, price FROM appointment " +
                        "LEFT JOIN appointment_result ON appointment.result_id = appointment_result.id ")

                val asData = buildList {
                    while (asResult.next()) {
                        val first = asResult.getTimestamp(1)?.toLocalDateTime()?.toLocalDate() ?: LocalDate.now()
                        val second = asResult.getFloat(2).roundToInt()
                        val datePrice = first to second
                        add(datePrice)
                    }
                }
                val parsedAsData = DataToStatisticsParser.parseForStatisticsAndCount(asData)

                ast = AppointmentStatistics(
                    amountPerDay = parsedAsData.first.perDay,
                    amountPerWeek = parsedAsData.first.perWeek,
                    amountPerMonth = parsedAsData.first.perMonth,
                    amountPerYear = parsedAsData.first.perYear,
                    moneyPerDay = parsedAsData.first.perDay,
                    moneyPerWeek = parsedAsData.first.perWeek,
                    moneyPerMonth = parsedAsData.first.perMonth,
                    moneyPerYear = parsedAsData.first.perYear
                )
            },
            scope.async(Dispatchers.IO) {
                // Hospitalizations
                val hsStatement = createStatement()
                val hsResult = hsStatement.executeQuery("SELECT start_date, price FROM hospitalization")

                val hsData = buildList {
                    while (hsResult.next()) {
                        val first = hsResult.getTimestamp(1)?.toLocalDateTime()?.toLocalDate() ?: LocalDate.now()
                        val second = hsResult.getFloat(2).roundToInt()
                        val datePrice = first to second
                        add(datePrice)
                    }
                }
                val parsedHsData = DataToStatisticsParser.parseForStatisticsAndCount(hsData)

                hs = HospitalizationStatistics(
                    amountPerDay = parsedHsData.first.perDay,
                    amountPerWeek = parsedHsData.first.perWeek,
                    amountPerMonth = parsedHsData.first.perMonth,
                    amountPerYear = parsedHsData.first.perYear,
                    moneyPerDay = parsedHsData.first.perDay,
                    moneyPerWeek = parsedHsData.first.perWeek,
                    moneyPerMonth = parsedHsData.first.perMonth,
                    moneyPerYear = parsedHsData.first.perYear
                )
            },
            scope.async(Dispatchers.IO) {
                // Additional payments
                val apStatement = createStatement()
                val apResult = apStatement.executeQuery("SELECT start_date, price FROM hospitalization")

                val apData = buildList {
                    while (apResult.next()) {
                        val first = apResult.getTimestamp(1)?.toLocalDateTime()?.toLocalDate() ?: LocalDate.now()
                        val second = apResult.getFloat(2).roundToInt()
                        val datePrice = first to second
                        add(datePrice)
                    }
                }
                val parsedApData = DataToStatisticsParser.parseForStatistics(apData)

                ap = AdditionalPaymentStatistics(
                    perDay = parsedApData.perDay,
                    perWeek = parsedApData.perWeek,
                    perMonth = parsedApData.perMonth,
                    perYear = parsedApData.perYear,
                )
            },
            scope.async(Dispatchers.IO) {
                // Salary
                val saStatement = createStatement()
                val saResult = saStatement.executeQuery("SELECT creation_date, salary FROM worker")

                val saData = buildList {
                    while (saResult.next()) {
                        val first = saResult.getTimestamp(1)?.toLocalDateTime()?.toLocalDate() ?: LocalDate.now()
                        val second = saResult.getFloat(2).roundToInt()
                        val datePrice = first to second
                        add(datePrice)
                    }
                }
                val parsedSaData = DataToStatisticsParser.parseForStatisticsAggregating(saData)

                sa = SalaryStatistics(
                    perDay = parsedSaData.perDay,
                    perWeek = parsedSaData.perWeek,
                    perMonth = parsedSaData.perMonth,
                    perYear = parsedSaData.perYear,
                )
            },
            scope.async(Dispatchers.IO) {
                // Registration
                val rewStatement = createStatement()
                val rewResult = rewStatement.executeQuery("SELECT creation_date FROM worker")

                val rewData = buildList {
                    while (rewResult.next()) {
                        add(rewResult.getTimestamp(1)?.toLocalDateTime()?.toLocalDate() ?: LocalDate.now())
                    }
                }
                val parsedRewData = DataToStatisticsParser.countForStatistics(rewData)

                val recStatement = createStatement()
                val recResult = recStatement.executeQuery("SELECT creation_date FROM medical_card")

                val recData = buildList {
                    while (recResult.next()) {
                        add(recResult.getTimestamp(1)?.toLocalDateTime()?.toLocalDate() ?: LocalDate.now())
                    }
                }
                val parsedRecData = DataToStatisticsParser.countForStatistics(recData)

                re = RegistrationStatistics(
                    workersPerDay = parsedRewData.perDay,
                    workersPerWeek = parsedRewData.perWeek,
                    workersPerMonth = parsedRewData.perMonth,
                    workersPerYear = parsedRewData.perYear,
                    clientsPerDay = parsedRecData.perDay,
                    clientsPerWeek = parsedRecData.perWeek,
                    clientsPerMonth = parsedRecData.perMonth,
                    clientsPerYear = parsedRecData.perYear,
                )
            },
            scope.async(Dispatchers.IO) {
                // Best doctors
                val bdStatement = createStatement()
                val bdResult = bdStatement.executeQuery("SELECT user_doctor.id, name, surname, login, COUNT(appointment.result_id), SUM(appointment_result.price) FROM worker " +
                        "LEFT JOIN user_doctor ON user_doctor.worker_id = worker.id " +
                        "LEFT JOIN public.user ON user_doctor.user_id = public.user.id " +
                        "LEFT JOIN appointment ON worker.id = appointment.doctor_id " +
                        "LEFT JOIN appointment_result ON appointment_result.id = appointment.result_id " +
                        "WHERE worker.can_receive_appointments = TRUE " +
                        "GROUP BY user_doctor.id, name, surname, login")

                bd = buildList {
                    while (bdResult.next()) {
                        add(
                            DoctorForStatistics(
                                userDoctorId = bdResult.getInt(1),
                                name = bdResult.getString(2),
                                surname = bdResult.getString(3),
                                login = bdResult.getString(4),
                                appointments = bdResult.getInt(5),
                                earnedMoney = bdResult.getInt(6)
                            )
                        )
                    }
                }
            },
            scope.async(Dispatchers.IO) {
                // Rooms
                val allStatement = createStatement()
                val allResult = allStatement.executeQuery("SELECT COUNT(*) FROM room " +
                        "JOIN room_location ON room.location_id = room_location.id " +
                        "JOIN room_type ON room_location.type_id = room_type.id " +
                        "WHERE room_type.id = 2")
                allResult.next()

                val busyStatement = createStatement()
                val busyResult = busyStatement.executeQuery("SELECT COUNT(*) FROM hospitalization WHERE end_date IS NULL")
                busyResult.next()

                rd = RoomDataForStatistics(
                    freeBeds = allResult.getInt(1) * 4 - busyResult.getInt(1),
                    busyBeds = busyResult.getInt(1)
                )
            },
            scope.async(Dispatchers.IO) {
                // Total workers and patients
                val twStatement = createStatement()
                val twResult = twStatement.executeQuery("SELECT COUNT(*) FROM worker")
                twResult.next()
                tw = twResult.getInt(1)

                val tpStatement = createStatement()
                val tpResult = tpStatement.executeQuery("SELECT COUNT(*) FROM medical_card")
                tpResult.next()
                tp = tpResult.getInt(1)
            }
        )

        val statisticsFetchData = StatisticsFetchData(
            appointmentStatistics = ast ?: AppointmentStatistics(),
            hospitalizationStatistics = hs ?: HospitalizationStatistics(),
            additionalPaymentStatistics = ap ?: AdditionalPaymentStatistics(),
            salaryStatistics = sa ?: SalaryStatistics(),
            totalMoneyStatistics = TotalMoneyStatistics.fromAllToChartTimeData(
                ast ?: AppointmentStatistics(),
                hs ?: HospitalizationStatistics(),
                ap ?: AdditionalPaymentStatistics()
            ),
            registrationStatistics = re ?: RegistrationStatistics(),
            bestDoctorsByAppointments = bd.sortedByDescending { it.earnedMoney },
            roomDataForStatistics = rd ?: RoomDataForStatistics(),
            totalWorkers = tw,
            totalPatients = tp
        )

        TransactorResult.Success(statisticsFetchData)
    }
}