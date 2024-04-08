package app_admin.di

import app_admin.data.*
import app_admin.domain.viewModel.*
import org.koin.dsl.*

val adminModule = module {
    single { WorkerRegistrationRepository(get()) }

    single { WorkerRegistrationViewModel(get()) }
}