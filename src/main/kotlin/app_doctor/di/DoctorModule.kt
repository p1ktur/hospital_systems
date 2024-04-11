package app_doctor.di

import app_doctor.data.*
import app_doctor.domain.viewModel.*
import org.koin.dsl.*

val doctorModule = module {
    single { DoctorLoginRepository(get()) }
    single { FindDoctorRepository(get()) }
    single { DoctorInfoRepository(get()) }
    single { DoctorScheduleRepository(get()) }

    factory { DoctorLoginViewModel(get()) }
    factory { FindDoctorViewModel(get()) }
    factory { DoctorInfoViewModel(get(), get()) }
    factory { DoctorScheduleViewModel(get()) }
}