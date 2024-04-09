package app_shared.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable
fun DefaultTextField(
    modifier: Modifier = Modifier,
    startValue: String,
    label: String,
    onValueChange: (String) -> Unit,
    showEditIcon: Boolean = true,
    onlyNumbers: Boolean = false
) {
    var text by remember {
        mutableStateOf(startValue)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(4.dp))
        if (showEditIcon) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.ModeEdit,
                contentDescription = "Edit icon",
                tint = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        BasicTextField(
            value = text,
            onValueChange = { newValue ->
                if (newValue.length <= 72) {
                    text = if (!onlyNumbers) newValue else newValue.filter { it.isDigit() }
                    onValueChange(text)
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onBackground,
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)
        )
    }
}