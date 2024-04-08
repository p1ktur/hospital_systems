package app_client.domain.uiEvent

sealed class ClientInfoUiEvent {
    data class FetchInfo(val userClientId: Int) : ClientInfoUiEvent()
}