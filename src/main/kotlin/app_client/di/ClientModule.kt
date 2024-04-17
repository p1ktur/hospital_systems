package app_client.di

import app_client.data.*
import app_client.domain.viewModel.*
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