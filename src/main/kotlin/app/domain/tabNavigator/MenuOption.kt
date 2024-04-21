package app.domain.tabNavigator

data class MenuOption(
    val text: String,
    val showOnlyOnWelcomeScreen: Boolean = false,
    val onClick: () -> Unit
)