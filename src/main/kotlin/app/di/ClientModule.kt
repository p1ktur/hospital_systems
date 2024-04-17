package app.di

import app.data.client.*
import app.domain.viewModel.client.*
import org.koin.dsl.*

val clientModule = module {
    single { ClientInfoRepository(get()) }
    single { ClientLoginRegistrationRepository(get()) }
    single { ClientsRepository(get()) }

    factory { ClientInfoViewModel(get()) }
    factory { ClientLoginViewModel(get()) }
    factory { ClientRegistrationViewModel(get()) }
    factory { ClientsViewModel(get()) }
}