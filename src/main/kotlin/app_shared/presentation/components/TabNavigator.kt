package app_shared.presentation.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import app_shared.domain.model.tabNavigator.*

@Composable
fun TabNavigator(
    navOptions: List<TabNavOption>,
    onNavigate: (String) -> Unit,
    onNavigateBack: () -> Unit,
    canGoBack: Boolean = true,
    isLoading: Boolean = false,
    isVisible: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val chosenIndicesStack = remember { mutableStateListOf(0) }
    var chosenIndex by remember { mutableIntStateOf(0) }
    var currentRoute by remember { mutableStateOf(navOptions.firstOrNull()?.route) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                navOptions.forEachIndexed { index, tabNavOption ->
                    Text(
                        modifier = Modifier
                            .clickable(onClick = {
                                chosenIndex = index
                                chosenIndicesStack.add(index)

                                if (currentRoute != tabNavOption.route) {
                                    currentRoute = tabNavOption.route
                                    onNavigate(tabNavOption.route)
                                }
                            })
                            .then(
                                if (chosenIndex == index) {
                                    Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                                } else {
                                    Modifier
                                }
                            )
                            .padding(8.dp),
                        text = tabNavOption.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (chosenIndex == index) {
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
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                content()
                if (isLoading) CircularProgressIndicator()
            }
            if (canGoBack) {
                Icon(
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.TopStart)
                        .clickable(onClick = {
                            if (chosenIndicesStack.isNotEmpty()) chosenIndicesStack.removeLast()
                            if (chosenIndicesStack.isNotEmpty()) chosenIndex = chosenIndicesStack.last()

                            onNavigateBack()
                        })
                        .padding(8.dp),
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}