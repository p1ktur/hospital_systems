package app.data.core

import app.domain.database.transactor.*
import java.io.*

class DatabaseDataExporter(private val transactor: ITransactor) {

    // To export: workers with schedule, patients with registration date, drugs, equipment (result: 6 tables used)
    fun exportDataToAFile(path: String): TransactorResult = transactor.startTransaction {
//        val file = File("C:/Users/kakap/IdeaProjects/HospitalSystems/src/main/resources/exported_data.json")
        val file = File("$path/exported_data.json")
        PrintWriter(file).apply {
            println()
            close()
        }
        file.appendText("{")

        val workersStatement = createStatement()
        val workersResult = workersStatement.executeQuery(
            "SELECT json_agg(" +
                "json_build_object(" +
                    "'name', name," +
                    "'surname', surname," +
                    "'fathers_name', fathers_name," +
                    "'age', age," +
                    "'address', address," +
                    "'phone', phone," +
                    "'email', email," +
                    "'salary', salary," +
                    "'can_receive_appointments', can_receive_appointments," +
                    "'registration_date', creation_date," +
                    "'schedule', json_build_object(" +
                        "'start_time', start_time," +
                        "'end_time', end_time," +
                        "'start_day', start_day," +
                        "'end_day', end_day," +
                        "'hours_for_rest', hours_for_rest" +
                    ")" +
                ")" +
            ") FROM worker LEFT JOIN schedule ON schedule.id = worker.schedule_id"
        )
        workersResult.next()

        file.appendText("\"workers\": ${workersResult.getString(1)},")

        val patientsStatement = createStatement()
        val patientsResult = patientsStatement.executeQuery(
            "SELECT json_agg(" +
                    "json_build_object(" +
                        "'name', name," +
                        "'surname', surname," +
                        "'fathers_name', fathers_name," +
                        "'age', age," +
                        "'address', address," +
                        "'phone', phone," +
                        "'email', email," +
                        "'registration_date', creation_date" +
                    ")" +
            ") FROM patient INNER JOIN medical_card ON patient.id = medical_card.patient_id"
        )
        patientsResult.next()

        file.appendText("\"patients\": ${patientsResult.getString(1)},")

        val drugsStatement = createStatement()
        val drugsResult = drugsStatement.executeQuery("SELECT json_agg(row_to_json(drug)) FROM drug")
        drugsResult.next()

        file.appendText("\"drugs\": ${drugsResult.getString(1)},")

        val equipmentsStatement = createStatement()
        val equipmentsResult = equipmentsStatement.executeQuery("SELECT json_agg(row_to_json(equipment)) FROM equipment")
        equipmentsResult.next()

        file.appendText("\"equipments\": ${equipmentsResult.getString(1)} }")

        TransactorResult.Success(file.path)
    }
}