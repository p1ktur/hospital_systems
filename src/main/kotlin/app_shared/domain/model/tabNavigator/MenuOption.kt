package app_shared.domain.model.tabNavigator

data class MenuOption(
    val text: String,
    val onClick: () -> Unit
)