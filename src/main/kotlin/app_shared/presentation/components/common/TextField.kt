package app_shared.presentation.components.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import app_shared.domain.model.regex.*

@Composable
fun DefaultTextField(
    modifier: Modifier = Modifier,
    startValue: String,
    label: String,
    onValueChange: (String) -> Unit,
    showEditIcon: Boolean = true,
    multiLine: Boolean = false,
    onlyNumbers: Boolean = false,
    onlyIntegerNumbers: Boolean = false,
    maxLength: Int = 72,
    isError: Boolean = false
) {
    var text by remember {
        mutableStateOf(startValue)
    }

    Row(
        modifier = modifier
    ) {
        androidx.compose.material3.Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(4.dp))
        if (showEditIcon) {
            androidx.compose.material3.Icon(
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
                if (newValue.length <= maxLength) {
                    text = if (!onlyNumbers && !onlyIntegerNumbers) {
                        newValue
                    } else if (!onlyIntegerNumbers) {
                        val tempValue = newValue.filter { it.isDigit() || it == '.' }
                        if (tempValue.count { it == '.' } > 1) text else tempValue
                    } else {
                        newValue.filter { it.isDigit() }
                    }
                    onValueChange(text)
                }
            },
            singleLine = !multiLine,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = if (!isError) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    MaterialTheme.colorScheme.error
                },
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OptionsTextField(
    modifier: Modifier = Modifier,
    startValue: String,
    label: String,
    showEditIcon: Boolean = true,
    decorated: Boolean = false,
    options: List<String> = emptyList(),
    onOptionSelected: (Int) -> Unit,
    isError: Boolean = false
) {
    var text by remember(startValue) {
        mutableStateOf(startValue)
    }

    var isMenuExpanded by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = options, block = {
        if (!options.contains(text)) text = options.firstOrNull().toString()
    })

    if (options.isEmpty()) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(4.dp))
            if (showEditIcon) {
                androidx.compose.material3.Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.ModeEdit,
                    contentDescription = "Edit icon",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            androidx.compose.material3.Text(
                text = "No options",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    } else {
        ExposedDropdownMenuBox(
            modifier = Modifier,
            expanded = isMenuExpanded,
            onExpandedChange = { newValue ->
                isMenuExpanded = newValue
            }
        ) {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(4.dp))
                if (showEditIcon) {
                    androidx.compose.material3.Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Edit icon",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    modifier = if (decorated) {
                        Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(5))
                            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(5))
                            .padding(4.dp)
                    } else {
                        Modifier
                    },
                    text = text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (!isError) {
                            if (decorated) {
                                MaterialTheme.colorScheme.onBackground
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                    )
                )
            }
            ExposedDropdownMenu(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .border(2.dp, MaterialTheme.colorScheme.primary),
                expanded = isMenuExpanded,
                onDismissRequest = {
                    isMenuExpanded = false
                }
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        onClick = {
                            text = option
                            onOptionSelected(index)
                            isMenuExpanded = false
                        }
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimeTextField(
    modifier: Modifier = Modifier,
    startValue: String,
    label: String,
    onValueChange: (String) -> Unit,
    showEditIcon: Boolean = true,
    isError: Boolean = false,
    onErrorChange: (Boolean) -> Unit
) {
    var text by remember {
        mutableStateOf(startValue)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(4.dp))
        if (showEditIcon) {
            androidx.compose.material3.Icon(
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
                    onErrorChange(!timeWithoutSecondsPattern.matches(newValue))

                    text = newValue
                    onValueChange(text)
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = if (!isError) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    MaterialTheme.colorScheme.error
                },
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)
        )
    }
}