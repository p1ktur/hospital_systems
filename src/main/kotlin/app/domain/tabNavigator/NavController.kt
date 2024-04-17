package app.domain.tabNavigator

import moe.tlaster.precompose.navigation.*

class NavController(val navigator: Navigator) {

    val canGoBack = navigator.canGoBack

    var navigateCallback: ((String) -> Unit)? = null
    var goBackCallback: (() -> Unit)? = null

    fun navigate(route: String) {
        navigateCallback?.invoke(route)
        navigator.navigate(route)
    }

    suspend fun navigateForResult(route: String): Any? {
        navigateCallback?.invoke(route)
        val result = navigator.navigateForResult(route)
        return result
    }

    fun goBack() {
        goBackCallback?.invoke()
        navigator.goBack()
    }

    fun goBackWith(result: Any? = null) {
        goBackCallback?.invoke()
        navigator.goBackWith(result)
    }

    fun clearBackStack() {
        navigator.goBack(PopUpTo(""))
    }

    fun compareRoutes(route1: String?, route2: String?): Boolean {
        if (route1 == null || route2 == null) return false

        return try {
            if (route1.startsWith("/") && route2.startsWith("/")) {
                route1.split("/")[1] == route2.split("/")[1]
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }
}