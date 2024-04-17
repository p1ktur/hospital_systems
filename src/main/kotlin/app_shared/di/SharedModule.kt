package app_shared.di

import app_shared.data.*
import app_shared.domain.model.database.transactor.*
import app_shared.domain.model.util.args.*
import app_shared.domain.model.util.vocabulary.*
import app_shared.domain.viewModel.*
import org.koin.dsl.*

val sharedModule = module {
    single<ITransactor> { (appArgs: AppArgs) ->
        val db = HospitalDatabase()
        db.init(appArgs)
        db
    }

    single { Vocabulary() }
    single { DatabaseInitializer(get(), get()) }

    single { AppointmentsRepository(get()) }
    single { HospitalizationsRepository(get()) }
    single { RoomsRepository(get()) }

    factory { AppointmentsViewModel(get()) }
    factory { HospitalizationsViewModel(get()) }
    factory { RoomsViewModel(get()) }
}