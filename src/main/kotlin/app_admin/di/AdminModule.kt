package app_admin.di

import app.domain.viewModel.doctor.*
import app_admin.data.*
import org.koin.dsl.*

val adminModule = module {
    single { WorkerRegistrationRepository(get()) }

    factory { DoctorRegistrationViewModel(get()) }
}