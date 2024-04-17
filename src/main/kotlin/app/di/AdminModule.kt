package app.di

import app.data.doctor.*
import app.domain.viewModel.doctor.*
import org.koin.dsl.*

val adminModule = module {
    single { DoctorLoginRegistrationRepository(get()) }

    factory { DoctorRegistrationViewModel(get()) }
}