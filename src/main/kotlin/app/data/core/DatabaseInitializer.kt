package app.data.core

import app.data.client.*
import app.data.doctor.*
import app.data.shared.*
import app.domain.database.transactor.*
import app.domain.model.shared.drug.*
import app.domain.util.vocabulary.*
import com.opencsv.*
import java.sql.*
import java.time.*
import java.time.temporal.ChronoUnit
import kotlin.random.*

class DatabaseInitializer(
    private val transactor: ITransactor,
    private val vocabulary: Vocabulary,
    private val clientLoginRegistrationRepository: ClientLoginRegistrationRepository,
    private val doctorLoginRegistrationRepository: DoctorLoginRegistrationRepository,
    private val appointmentsRepository: AppointmentsRepository,
    private val hospitalizationsRepository: HospitalizationsRepository,
    private val paymentsRepository: PaymentsRepository
) {

    fun initializeDatabaseWithResetOnFailure(): TransactorResult = transactor.startTransaction(onSQLException = { deleteInitData() }) {
        val random = Random(System.currentTimeMillis())

        //init db: room, room_location, room_type, drugs, medical_equipment

        //room type names: Doctor, Staff
        //room locations: floors: 5, number: 101 - 120, 1-2 floors are staff, 3-5 floors are doctor
        //room for each location if staff -> ward, if doctor -> position name

        val (roomLocationIndex, patientRoomsCounter) = initRooms()
        initDrugs(random)
        initEquipment(random, roomLocationIndex, patientRoomsCounter)
        initUsers(random)
        initAppointments(random)
        initHospitalizations(random)
        initSubPayments(random)

        println("Initialization successful")

        TransactorResult.Success("Success")
    }

    fun deleteInitData(): TransactorResult = transactor.startTransaction {
        val deleteStatement = createStatement()
        deleteStatement.executeUpdate(
            "TRUNCATE equipment CASCADE; " +               // +
                    "TRUNCATE drug CASCADE; " +                 // +
                    "TRUNCATE room CASCADE; " +                 // +
                    "TRUNCATE room_location CASCADE; " +        // +
                    "TRUNCATE room_type CASCADE; " +            // +
                    "TRUNCATE user_client CASCADE; " +          // +
                    "TRUNCATE patient CASCADE; " +              // +
                    "TRUNCATE medical_card CASCADE; " +         // +
                    "TRUNCATE user_doctor CASCADE; " +          // +
                    "TRUNCATE worker CASCADE; " +               // +
                    "TRUNCATE schedule CASCADE;" +              // +
                    "TRUNCATE public.user CASCADE; " +          // +
                    "TRUNCATE appointment CASCADE; " +          // +
                    "TRUNCATE appointment_result CASCADE; " +   // +
                    "TRUNCATE hospitalization CASCADE; " +      // +
                    "TRUNCATE payment CASCADE; " +              // +
                    "TRUNCATE sub_payment CASCADE; "            //
        )

        TransactorResult.Success("Success")
    }

    private fun Connection.initRooms(): Pair<Int, Int> {
        val roomTypeStatement = createStatement()
        roomTypeStatement.executeUpdate("INSERT INTO room_type (id, name) VALUES (1, 'Doctor'), (2, 'Ward') ON CONFLICT DO NOTHING")

        val roomLocationStatement =
            prepareStatement("INSERT INTO room_location (id, type_id, floor, number) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING")
        val roomStatement = prepareStatement("INSERT INTO room (id, location_id, name) VALUES (?, ?, ?) ON CONFLICT DO NOTHING")

        // total floors = 5
        var roomLocationIndex = 0
        var patientRoomsCounter = 0

        val patientNumbers = 20 // floors 1-2
        val doctorNumbers = 10 // floors 3-5

        for (i in 1..2) {
            for (j in 1..patientNumbers) {
                roomLocationStatement.setInt(1, ++roomLocationIndex)
                roomLocationStatement.setInt(2, 2)
                roomLocationStatement.setInt(3, i)
                roomLocationStatement.setInt(4, 100 * i + j)

                roomStatement.setInt(1, roomLocationIndex)
                roomStatement.setInt(2, roomLocationIndex)
                roomStatement.setString(3, "Ward ${++patientRoomsCounter}")

                roomLocationStatement.executeUpdate()
                roomStatement.executeUpdate()
            }
        }

        for (i in 3..5) {
            for (j in 1..doctorNumbers) {
                roomLocationStatement.setInt(1, ++roomLocationIndex)
                roomLocationStatement.setInt(2, 1)
                roomLocationStatement.setInt(3, i)
                roomLocationStatement.setInt(4, 100 * i + j)

                roomStatement.setInt(1, roomLocationIndex)
                roomStatement.setInt(2, roomLocationIndex)
                roomStatement.setString(3, vocabulary.doctorPositions[10 * (i - 3) + j - 1].second)

                roomLocationStatement.executeUpdate()
                roomStatement.executeUpdate()
            }
        }

        val roomLocationSequenceStatement = createStatement()
        roomLocationSequenceStatement.executeUpdate("ALTER SEQUENCE room_location_id_seq RESTART WITH $roomLocationIndex")

        val roomSequenceStatement = createStatement()
        roomSequenceStatement.executeUpdate("ALTER SEQUENCE room_id_seq RESTART WITH $roomLocationIndex")

        return roomLocationIndex to patientRoomsCounter
    }

    private fun Connection.initDrugs(random: Random) {
        val drugStatement = prepareStatement("INSERT INTO drug (id, name, appliances, notes, amount) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING")
        val parsedDrugs = parseDrugsCSV(random)
        parsedDrugs.forEach { drug ->
            drugStatement.setInt(1, drug.id)
            drugStatement.setString(2, drug.name)
            drugStatement.setString(3, drug.appliances)
            drugStatement.setString(4, drug.notes)
            drugStatement.setInt(5, drug.amount)

            drugStatement.executeUpdate()
        }

        val drugSequenceStatement = createStatement()
        drugSequenceStatement.executeUpdate("ALTER SEQUENCE drug_id_seq RESTART WITH ${parsedDrugs.last().id}")
    }

    private fun Connection.initEquipment(random: Random, roomLocationIndex: Int, patientRoomsCounter: Int) {
        val equipmentStatement = prepareStatement("INSERT INTO equipment (id, room_id, name, notes) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING")

        var equipmentIndex = 0
        for (i in 1..roomLocationIndex) {
            for (j in 0 until random.nextInt(3, 6)) {
                equipmentStatement.setInt(1, ++equipmentIndex)
                equipmentStatement.setInt(2, i)
                equipmentStatement.setString(
                    3,
                    if (i <= patientRoomsCounter) {
                        vocabulary.wardEquipment.random(random)
                    } else {
                        vocabulary.officeEquipment.random(random)
                    }
                )
                equipmentStatement.setString(4, "")

                equipmentStatement.executeUpdate()
            }
        }

        val equipmentSequenceStatement = createStatement()
        equipmentSequenceStatement.executeUpdate("ALTER SEQUENCE equipment_id_seq RESTART WITH $equipmentIndex")
    }

    private fun Connection.initUsers(random: Random) {
        val clients = 150..180
        val workers = 60..80
        for (i in 0 until random.nextInt(clients)) {
            val name = vocabulary.names.random(random)
            val surname = vocabulary.surnames.random(random)
            val login = "${name}_${vocabulary.nicknames.random(random)}_${1985 + random.nextInt(35)}"
            val password = "${name}1234"

            clientLoginRegistrationRepository.register(
                name = name,
                surname = surname,
                fathersName = vocabulary.fatherNames.random(random),
                age = random.nextInt(20, 70).toString(),
                address = vocabulary.addresses.random(random),
                phone = vocabulary.phones.random(random),
                email = "$name.$surname@gmail.com",
                login = login,
                password = password
            )
        }

        val userClientStatement = createStatement()
        val userClientResult = userClientStatement.executeQuery("SELECT medical_card_id FROM user_client")

        val clientStatement = prepareStatement("UPDATE medical_card SET creation_date = ? WHERE id = ?")
        while (userClientResult.next()) {
            clientStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().minusDays(random.nextLong(1, 365))))
            clientStatement.setInt(2, userClientResult.getInt(1))
            clientStatement.executeUpdate()
        }

        for (i in 0 until random.nextInt(workers)) {
            val name = vocabulary.names.random(random)
            val surname = vocabulary.surnames.random(random)
            val login = "${name}_${vocabulary.nicknames.random(random)}_${1985 + random.nextInt(35)}"
            val password = "${name}1234"

            doctorLoginRegistrationRepository.register(
                name = name,
                surname = surname,
                fathersName = vocabulary.fatherNames.random(random),
                age = random.nextInt(20, 70).toString(),
                address = vocabulary.addresses.random(random),
                phone = vocabulary.phones.random(random),
                email = "$name.$surname@gmail.com",
                position = vocabulary.doctorPositions.random(random).first,
                salary = random.nextInt(500, 1000).toString(),
                canReceiveAppointments = random.nextBoolean(),
                login = login,
                password = password
            )
        }

        val userDoctorStatement = createStatement()
        val userDoctorResult = userDoctorStatement.executeQuery("SELECT worker_id FROM user_doctor")

        val scheduleStatement = prepareStatement("INSERT INTO schedule (start_time, end_time, start_day, end_day, hours_for_rest) VALUES (?, ?, ?, ?, ?) RETURNING id")
        val doctorStatement = prepareStatement("UPDATE worker SET creation_date = ?, schedule_id = ?, room_id = ? WHERE id = ?")
        while (userDoctorResult.next()) {
            val startTime = random.nextLong(6, 10)
            val startDayIndex = random.nextInt(0, 2)
            scheduleStatement.setTime(1, Time.valueOf(LocalTime.MIN.plusHours(startTime)))
            scheduleStatement.setTime(2, Time.valueOf(LocalTime.MIN.plusHours(startTime + random.nextLong(8, 10))))
            scheduleStatement.setString(3, vocabulary.daysOfWeek[startDayIndex])
            scheduleStatement.setString(4, vocabulary.daysOfWeek[startDayIndex + random.nextInt(3, 5)])
            scheduleStatement.setFloat(5, random.nextInt(1, 3).toFloat())
            val scheduleResult = scheduleStatement.executeQuery()
            scheduleResult.next()

            val roomStatement = createStatement()
            val roomResult = roomStatement.executeQuery("SELECT room.id FROM room " +
                    "JOIN room_location ON room.location_id = room_location.id " +
                    "JOIN room_type ON room_location.type_id = room_type.id " +
                    "WHERE room_type.id = 1 " +
                    "ORDER BY random() LIMIT 1")
            roomResult.next()

            doctorStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().minusDays(random.nextLong(1, 365))))
            doctorStatement.setInt(2, scheduleResult.getInt(1))
            doctorStatement.setInt(3, roomResult.getInt(1))
            doctorStatement.setInt(4, userDoctorResult.getInt(1))
            doctorStatement.executeUpdate()
        }
    }

    private fun Connection.initAppointments(random: Random) {
        val now = LocalDateTime.now()
        val allAppointments = 250..300
        val endedAppointments = 225..250
        val payedAppointments = 200..225

        val userClientStatement = createStatement()
        val userClientResult = userClientStatement.executeQuery("SELECT user_client.id, medical_card_id, creation_date FROM user_client " +
                "JOIN medical_card ON user_client.medical_card_id = medical_card.id")

        val clients = buildList {
            while (userClientResult.next()) {
                add(Triple(userClientResult.getInt(1), userClientResult.getInt(2), userClientResult.getTimestamp(3).toLocalDateTime()))
            }
        }

        val userDoctorStatement = createStatement()
        val userDoctorResult = userDoctorStatement.executeQuery("SELECT user_doctor.id, worker_id, creation_date FROM user_doctor " +
                "JOIN worker ON user_doctor.worker_id = worker.id")

        val doctors = buildList {
            while (userDoctorResult.next()) {
                add(Triple(userDoctorResult.getInt(1), userDoctorResult.getInt(2), userDoctorResult.getTimestamp(3).toLocalDateTime()))
            }
        }

        for (i in 0 until random.nextInt(allAppointments)) {
            val randomClient = clients.random(random)
            val randomDoctor = doctors.random(random)
            val lastDate = if (randomDoctor.third.isAfter(randomClient.third)) randomDoctor.third else randomClient.third
            val newTime = now.minusDays(random.nextLong(ChronoUnit.DAYS.between(lastDate, now) + 1))

            appointmentsRepository.createAppointment(randomDoctor.first, randomClient.first, newTime)
        }

        val appointmentStatement = createStatement()
        val appointmentResult = appointmentStatement.executeQuery("SELECT id FROM appointment")

        val appointments = buildList {
            while (appointmentResult.next()) {
                add(appointmentResult.getInt(1))
            }
        }.shuffled(random).subList(0, random.nextInt(endedAppointments))

        appointments.forEach { id ->
            appointmentsRepository.createAppointmentResult(id, random.nextInt(50, 150).toFloat(), vocabulary.notes.random(random))
        }

        val appointmentResultStatement = createStatement()
        val appointmentResultResult = appointmentResultStatement.executeQuery("SELECT appointment_result.id, price, login FROM appointment_result " +
                "INNER JOIN appointment ON appointment_result.id = appointment.result_id " +
                "INNER JOIN medical_card ON appointment.medical_card_id = medical_card.id " +
                "INNER JOIN user_client ON user_client.medical_card_id = medical_card.id " +
                "INNER JOIN public.user ON user_client.user_id = public.user.id")

        val appointmentResults = buildList {
            while (appointmentResultResult.next()) {
                add(Triple(appointmentResultResult.getInt(1), appointmentResultResult.getFloat(2), appointmentResultResult.getString(3)))
            }
        }.shuffled(random).subList(0, random.nextInt(payedAppointments))

        appointmentResults.forEach { (id, amount, account) ->
            appointmentsRepository.payForAppointment(id, amount, "@$account\'s account")
        }

        val updatePaymentStatement = prepareStatement("UPDATE payment SET time = ? WHERE id = ?")

        val newPaymentStatement = createStatement()
        val newPaymentResult = newPaymentStatement.executeQuery("SELECT payment.id, appointment.date FROM payment " +
                "INNER JOIN appointment_result ON appointment_result.payment_id = payment.id " +
                "INNER JOIN appointment ON appointment.result_id = appointment_result.id")

        while (newPaymentResult.next()) {
            val lastDate = newPaymentResult.getTimestamp(2).toLocalDateTime()
            val newTime = now.minusDays(random.nextLong(ChronoUnit.DAYS.between(lastDate, now) + 1))

            updatePaymentStatement.setTimestamp(1, Timestamp.valueOf(newTime))
            updatePaymentStatement.setInt(2, newPaymentResult.getInt(1))
            updatePaymentStatement.executeUpdate()
        }
    }

    private fun Connection.initHospitalizations(random: Random) {
        val now = LocalDateTime.now()
        val allHospitalizations = 200..350
        val endedHospitalizationsRange = 150..200
        val payedHospitalizations = 125..150

        val userClientStatement = createStatement()
        val userClientResult = userClientStatement.executeQuery("SELECT user_client.id, medical_card_id, creation_date FROM user_client " +
                "JOIN medical_card ON user_client.medical_card_id = medical_card.id")

        val clients = buildList {
            while (userClientResult.next()) {
                add(Triple(userClientResult.getInt(1), userClientResult.getInt(2), userClientResult.getTimestamp(3).toLocalDateTime()))
            }
        }

        val wardStatement = createStatement()
        val wardResult = wardStatement.executeQuery("SELECT room.id FROM room " +
                "JOIN room_location ON room.location_id = room_location.id " +
                "JOIN room_type ON room.location_id = room_location.id " +
                "WHERE room_type.id = 2")

        val wards = buildList {
            while (wardResult.next()) {
                add(wardResult.getInt(1))
            }
        }

        for (i in 0 until random.nextInt(allHospitalizations)) {
            val randomClient = clients.random(random)
            val randomRoom = wards.random(random)

            hospitalizationsRepository.createHospitalization(randomClient.first, randomRoom, vocabulary.reasons.random(random), random.nextInt(50, 150).toFloat())
        }

        val hospitalizationStatement = createStatement()
        val hospitalizationResult = hospitalizationStatement.executeQuery("SELECT hospitalization.id, creation_date FROM hospitalization " +
                "INNER JOIN medical_card ON hospitalization.medical_card_id = medical_card.id")

        val updateHospitalizationStatement = prepareStatement("UPDATE hospitalization SET start_date = ? WHERE id = ?")
        while (hospitalizationResult.next()) {
            val lastDate = hospitalizationResult.getTimestamp(2).toLocalDateTime()
            val newTime = now.minusDays(random.nextLong(ChronoUnit.DAYS.between(lastDate, now) + 1))

            updateHospitalizationStatement.setTimestamp(1, Timestamp.valueOf(newTime))
            updateHospitalizationStatement.setInt(2, hospitalizationResult.getInt(1))
            updateHospitalizationStatement.executeUpdate()
        }

        val newHospitalizationStatement = createStatement()
        val newHospitalizationResult = newHospitalizationStatement.executeQuery("SELECT id, start_date FROM hospitalization")

        val hospitalizations = buildList {
            while (newHospitalizationResult.next()) {
                add(newHospitalizationResult.getInt(1) to newHospitalizationResult.getTimestamp(2).toLocalDateTime())
            }
        }.shuffled(random).subList(0, random.nextInt(endedHospitalizationsRange))

        val updateNewHospitalizationStatement = prepareStatement("UPDATE hospitalization SET end_date = ? WHERE id = ?")
        hospitalizations.forEach { (id, startDate) ->
            val newTime = now.minusDays(random.nextLong(ChronoUnit.DAYS.between(startDate, now) + 1))

            hospitalizationsRepository.endHospitalization(id)
            updateNewHospitalizationStatement.setTimestamp(1, Timestamp.valueOf(newTime))
            updateNewHospitalizationStatement.setInt(2, id)
            updateNewHospitalizationStatement.executeUpdate()
        }

        val endedHospitalizationStatement = createStatement()
        val endedHospitalizationResult = endedHospitalizationStatement.executeQuery("SELECT hospitalization.id, price, login FROM hospitalization " +
                "INNER JOIN medical_card ON hospitalization.medical_card_id = medical_card.id " +
                "INNER JOIN user_client ON user_client.medical_card_id = medical_card.id " +
                "INNER JOIN public.user ON user_client.user_id = public.user.id " +
                "WHERE end_date IS NOT NULL")

        val endedHospitalizations = buildList {
            while (endedHospitalizationResult.next()) {
                add(Triple(endedHospitalizationResult.getInt(1), endedHospitalizationResult.getFloat(2), endedHospitalizationResult.getString(3)))
            }
        }.shuffled(random).subList(0, random.nextInt(payedHospitalizations))

        endedHospitalizations.forEach { (id, amount, account) ->
            hospitalizationsRepository.payForHospitalization(id, amount, "@$account\'s account")
        }

        val updatePaymentStatement = prepareStatement("UPDATE payment SET time = ? WHERE id = ?")

        val newPaymentStatement = createStatement()
        val newPaymentResult = newPaymentStatement.executeQuery("SELECT payment.id, hospitalization.start_date FROM payment " +
                "INNER JOIN hospitalization ON hospitalization.payment_id = payment.id")

        while (newPaymentResult.next()) {
            val lastDate = newPaymentResult.getTimestamp(2).toLocalDateTime()
            val newTime = now.minusDays(random.nextLong(ChronoUnit.DAYS.between(lastDate, now) + 1))

            updatePaymentStatement.setTimestamp(1, Timestamp.valueOf(newTime))
            updatePaymentStatement.setInt(2, newPaymentResult.getInt(1))
            updatePaymentStatement.executeUpdate()
        }
    }

    private fun Connection.initSubPayments(random: Random) {
        val now = LocalDateTime.now()
        val subPaymentsRange = 200..250
        val payedSubPaymentsRange = 175..200

        val userClientStatement = createStatement()
        val userClientResult = userClientStatement.executeQuery("SELECT user_client.id, medical_card_id, creation_date FROM user_client " +
                "JOIN medical_card ON user_client.medical_card_id = medical_card.id")

        val clients = buildList {
            while (userClientResult.next()) {
                add(Triple(userClientResult.getInt(1), userClientResult.getInt(2), userClientResult.getTimestamp(3).toLocalDateTime()))
            }
        }

        for (i in 0 until random.nextInt(subPaymentsRange)) {
            paymentsRepository.createSubPayment(clients.random(random).first, vocabulary.subjects.random(random), random.nextInt(50, 150).toFloat())
        }

        val subPaymentStatement = createStatement()
        val subPaymentResult = subPaymentStatement.executeQuery("SELECT sub_payment.id, to_pay_amount, login, creation_date FROM sub_payment " +
                "INNER JOIN medical_card ON sub_payment.medical_card_id = medical_card.id " +
                "INNER JOIN user_client ON user_client.medical_card_id = medical_card.id " +
                "INNER JOIN public.user ON user_client.user_id = public.user.id")

        val subPayments = buildList {
            while (subPaymentResult.next()) {
                add(
                    Triple(subPaymentResult.getInt(1), subPaymentResult.getFloat(2), subPaymentResult.getString(3)) to
                            subPaymentResult.getTimestamp(4).toLocalDateTime()
                )
            }
        }.shuffled(random).subList(0, random.nextInt(payedSubPaymentsRange))

        val updateSubPaymentStatement = prepareStatement("UPDATE sub_payment SET time = ? WHERE id = ?")

        subPayments.forEach { (other, date) ->
            val (id, amount, account) = other
            paymentsRepository.payForSubPayment(id, amount, "@$account\'s account")

            val newTime = now.minusDays(random.nextLong(ChronoUnit.DAYS.between(date, now) + 1))

            updateSubPaymentStatement.setTimestamp(1, Timestamp.valueOf(newTime))
            updateSubPaymentStatement.setInt(2, id)
            updateSubPaymentStatement.executeUpdate()
        }
    }

    private fun parseDrugsCSV(random: Random): List<Drug> {
        return try {
            val drugsList = mutableListOf<Drug>()
            val file = javaClass.classLoader.getResourceAsStream("drugs_data.csv") ?: return emptyList()
            val reader = CSVReader(file.reader())

            val namesCache = mutableListOf<String>()
            var idCounter = 0
            var line: Array<String>? = reader.readNext()
            while (!line.isNullOrEmpty()) {
                if (!namesCache.contains(line[0]) && line[7].length <= 256) {
                    namesCache.add(line[0])
                    drugsList.add(
                        Drug(
                            id = ++idCounter,
                            name = line[0],
                            appliances = line[7],
                            notes = "",
                            amount = random.nextInt(100, 1000)
                        )
                    )
                }

                line = reader.readNext()
            }

            drugsList
        } catch (e: Exception) {
            println(e.message)
            emptyList()
        }
    }
}