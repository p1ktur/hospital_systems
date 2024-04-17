package app.presentation.components.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*

@Composable
fun ReducedText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onBackground,
    reduceWidth: Int = 160
) {
    var isReduced by remember(text) { mutableStateOf(true) }

    Text(
        modifier = modifier
            .clickable(
                onClick = {
                    isReduced = !isReduced
                }
            )
            .then(
                if (isReduced) {
                    Modifier.width(reduceWidth.dp)
                } else {
                    Modifier
                }
            ),
        text = text,
        style = style,
        color = color,
        overflow = TextOverflow.Ellipsis,
        maxLines = if (isReduced) 1 else Int.MAX_VALUE
    )
}