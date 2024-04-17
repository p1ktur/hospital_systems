package app.di

import app.data.doctor.*
import app.domain.viewModel.doctor.*
import org.koin.dsl.*

val doctorModule = module {
    single { DoctorLoginRegistrationRepository(get()) }
    single { DoctorsRepository(get()) }
    single { DoctorInfoRepository(get()) }
    single { DoctorScheduleRepository(get()) }

    factory { DoctorRegistrationViewModel(get()) }
    factory { DoctorLoginViewModel(get()) }
    factory { DoctorsViewModel(get()) }
    factory { DoctorInfoViewModel(get(), get()) }
    factory { DoctorScheduleViewModel(get()) }
}