package app_client.di

import app_client.data.*
import app_client.domain.viewModel.*
import org.koin.dsl.*

val clientModule = module {
    single { ClientInfoRepository(get()) }

    single { ClientInfoViewModel(get()) }
    single { ClientRegistrationViewModel(get()) }
}