package app.domain.model.shared.charts

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*

data class ChartSettings(
    var yTitle: String,
    var titleStyle: TextStyle,
    var dataTextStyle: TextStyle,
    var backgroundColor: Color,
    var linesColor: Color,
    var textColor: Color,
    var dataColor: Color = Color(0xFFD7930E)
)

@Composable
fun defaultChartSettings(): ChartSettings {
    return ChartSettings(
        yTitle = "Value, %",
        titleStyle = MaterialTheme.typography.bodyLarge,
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        linesColor = MaterialTheme.colorScheme.onPrimaryContainer,
        textColor = MaterialTheme.colorScheme.onBackground,
        dataTextStyle = MaterialTheme.typography.bodySmall
    )
}