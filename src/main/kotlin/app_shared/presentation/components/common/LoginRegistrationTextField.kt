package app_shared.presentation.components.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import java.util.*

@Composable
fun LoginRegistrationTextField(
    label: String,
    startValue: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    errorText: String?
) {
    var text by remember {
        mutableStateOf(startValue)
    }

    var showPassword by remember {
        mutableStateOf(false)
    }

    TextField(
        modifier = Modifier.width(320.dp),
        value = text,
        onValueChange = { newValue ->
            text = newValue
            onValueChange(text)
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        label = {
            Text(
                text = errorText ?: label,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        placeholder = {
            Text(
                text = "Type ${label.replaceFirstChar { it.lowercase(Locale.getDefault()) }}",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                Icon(
                    modifier = Modifier.clickable(
                        onClick = {
                            showPassword = !showPassword
                        }
                    ),
                    imageVector = if (!showPassword) {
                        Icons.Default.LockOpen
                    } else {
                        Icons.Default.Lock
                    },
                    contentDescription = "Show password"
                )
            }
        },
        isError = errorText != null
    )
}