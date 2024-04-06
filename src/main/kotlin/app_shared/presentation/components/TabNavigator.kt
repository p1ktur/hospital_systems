package app_shared.presentation.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import app_shared.domain.model.tabNavigator.*

@Composable
fun TabNavigator(
    navOptions: List<TabNavOption>,
    onNavigate: (String) -> Unit,
    isLoading: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navOptions.forEachIndexed { index, tabNavOption ->
                Text(
                    modifier = Modifier
                        .clickable(onClick = { onNavigate(tabNavOption.route) })
                        .padding(8.dp),
                    text = tabNavOption.name,
                    style = MaterialTheme.typography.titleSmall
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content()
            if (isLoading) CircularProgressIndicator()
        }
    }
}