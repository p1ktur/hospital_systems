package app.domain.tabNavigator

data class MenuOption(
    val text: String,
    val onClick: () -> Unit
)