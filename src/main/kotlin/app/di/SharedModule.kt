package app.di

import app.data.core.*
import app.data.shared.*
import app.domain.database.transactor.*
import app.domain.util.args.*
import app.domain.util.vocabulary.*
import app.domain.viewModel.shared.*
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