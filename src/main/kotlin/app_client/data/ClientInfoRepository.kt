package app_client.data

import app_shared.domain.model.transactor.*

class ClientInfoRepository(private val transactor: ITransactor) {

    fun register(
        name: String,
        surname: String,
        fathersName: String,
        age: String,
        address: String,
        phone: String,
        email: String,
        login: String,
        password: String
    ): TransactorResult {


        return TransactorResult.Failure()
    }
}