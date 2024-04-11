package app_shared.di

import app_shared.data.*
import app_shared.domain.model.args.*
import app_shared.domain.model.transactor.*
import app_shared.domain.model.vocabulary.*
import org.koin.dsl.*

val sharedModule = module {
    single<ITransactor> { (appArgs: AppArgs) ->
        val db = HospitalDatabase()
        db.init(appArgs)
        db
    }

    single { Vocabulary() }
    single { DatabaseInitializer(get(), get()) }
}