package app.data.shared

import app.domain.database.transactor.*
import app.domain.model.shared.drug.*

class DrugsRepository(private val transactor: ITransactor) {

    fun search(): TransactorResult = transactor.startTransaction {
        val searchStatement = prepareStatement("SELECT id, name, appliances, notes, amount FROM drug")
        val searchResult = searchStatement.executeQuery()

        val drugSearchDataList = buildList {
            while (searchResult.next()) add(
                Drug(
                    id = searchResult.getInt(1),
                    name = searchResult.getString(2),
                    appliances = searchResult.getString(3),
                    notes = searchResult.getString(4),
                    amount = searchResult.getInt(5)
                )
            )
        }

        TransactorResult.Success(drugSearchDataList.sortedBy { it.name })
    }

    fun create(drug: Drug): TransactorResult = transactor.startTransaction {
        val createStatement = prepareStatement("INSERT INTO drug (name, appliances, notes, amount) VALUES (?, ?, ?, ?);")
        createStatement.setString(1, drug.name)
        createStatement.setString(2, drug.appliances)
        createStatement.setString(3, drug.notes)
        createStatement.setInt(4, drug.amount)
        createStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun update(drug: Drug): TransactorResult = transactor.startTransaction {
        val updateStatement = prepareStatement("UPDATE drug SET name = ?, appliances = ?, notes = ?, amount = ? WHERE id = ?")
        updateStatement.setString(1, drug.name)
        updateStatement.setString(2, drug.appliances)
        updateStatement.setString(3, drug.notes)
        updateStatement.setInt(4, drug.amount)
        updateStatement.setInt(5, drug.id)
        updateStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun delete(drug: Drug): TransactorResult = transactor.startTransaction {
        val deleteStatement = prepareStatement("DELETE FROM drug WHERE id = ?")
        deleteStatement.setInt(1, drug.id)
        deleteStatement.executeUpdate()

        TransactorResult.Success("Success")
    }
}