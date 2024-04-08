package app_doctor.di

import app_doctor.data.*
import app_doctor.domain.viewModel.*
import org.koin.dsl.*

val doctorModule = module {
    single { DoctorLoginRegistrationRepository(get()) }

    single { ClientRegistrationViewModel(get()) }
    single { DoctorLoginViewModel(get()) }
}