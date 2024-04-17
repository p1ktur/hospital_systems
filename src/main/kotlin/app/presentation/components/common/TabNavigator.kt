package app.presentation.components.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import app.domain.tabNavigator.*
import moe.tlaster.precompose.navigation.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TabNavigator(
    navOptions: List<TabNavOption>,
    isLoading: Boolean = false,
    onLogOut: (() -> Unit)? = null,
    content: @Composable BoxScope.(NavController) -> Unit
) {
    val navigationRouteStack = remember { mutableStateListOf<String>() }
    var currentRoute by remember { mutableStateOf<String?>(null) }

    val navigator = rememberNavigator()
    val navController by remember(navigator) {
        val navController = NavController(navigator).apply {
            navigateCallback = { route ->
                navigationRouteStack.add(route)
                currentRoute = route
            }
            goBackCallback = {
                if (navigationRouteStack.isNotEmpty()) navigationRouteStack.removeLast()
                if (navigationRouteStack.isNotEmpty()) currentRoute = navigationRouteStack.last()
            }
        }

        mutableStateOf(navController)
    }
    val canNavControllerGoBack by navController.canGoBack.collectAsState(false)
    val currentEntry by navigator.currentEntry.collectAsState(null)

    var isMenuExpanded by remember { mutableStateOf(false) }
    val menuOptions = remember {
        val list = mutableStateListOf<MenuOption>()
        if (onLogOut != null) list.add(
            MenuOption(
                text = "Log out",
                onClick = {
                    onLogOut.invoke()

                    navController.clearBackStack()
                    if (navigationRouteStack.size > 1) navigationRouteStack.removeRange(1, navigationRouteStack.size)
                }
            )
        )
        list
    }

    LaunchedEffect(key1 = true, block = {
        if (navOptions.isNotEmpty()) {
            navigationRouteStack.add(navOptions[0].route)
        }
    })

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                navOptions.forEachIndexed { index, tabNavOption ->
                    val firstCheck = navController.compareRoutes(currentRoute, tabNavOption.route)
                    val secondCheck = navOptions.any { option ->
                        currentEntry?.route?.route?.let {
                            navController.compareRoutes(it, option.route)
                        } ?: false
                    }

                    Text(
                        modifier = Modifier
                            .clickable(onClick = {
                                if (!navController.compareRoutes(currentRoute, tabNavOption.route)) {
                                    currentRoute = tabNavOption.route
                                    navController.navigate(tabNavOption.route)
                                }
                            })
                            .then(
                                if (firstCheck && secondCheck) {
                                    Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                                } else {
                                    Modifier
                                }
                            )
                            .padding(8.dp),
                        text = tabNavOption.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (firstCheck && secondCheck) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        }
                    )
                    if (index != navOptions.size - 1) {
                        Divider(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                                .padding(vertical = 4.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            if (menuOptions.isNotEmpty()) {
                Icon(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(onClick = {
                            isMenuExpanded = true
                        })
                        .padding(8.dp),
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                content(navController)
                if (isLoading) CircularProgressIndicator()
            }
            if (canNavControllerGoBack) {
                Icon(
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.TopStart)
                        .clickable(onClick = {
                            navController.goBack()
                        })
                        .padding(8.dp),
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            if (menuOptions.isNotEmpty()) {
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .width(200.dp),
                    expanded = isMenuExpanded,
                    onExpandedChange = { newValue ->
                        isMenuExpanded = newValue
                    }
                ) {
                    ExposedDropdownMenu(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .border(2.dp, MaterialTheme.colorScheme.primary),
                        expanded = isMenuExpanded,
                        onDismissRequest = {
                            isMenuExpanded = false
                        }
                    ) {
                        menuOptions.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    option.onClick()
                                    isMenuExpanded = false
                                }
                            ) {
                                Text(
                                    text = option.text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}