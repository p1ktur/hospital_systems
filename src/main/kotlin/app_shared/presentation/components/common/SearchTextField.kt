package app_shared.presentation.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*

@Composable
fun SearchTextField(
    startValue: String,
    onValueChange: (String) -> Unit
) {
    var text by remember {
        mutableStateOf(startValue)
    }

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = text,
        onValueChange = { newValue ->
            text = newValue
            onValueChange(text)
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        label = {
            Text(
                text = "Search",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        placeholder = {
            Text(
                text = "Type name, surname, age, phone or login",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Show password"
            )
        }
    )
}