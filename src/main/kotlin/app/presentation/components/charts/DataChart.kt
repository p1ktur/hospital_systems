package app.presentation.components.charts

import androidx.compose.desktop.ui.tooling.preview.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import app.domain.model.shared.charts.*
import app.domain.theme.*
import app.domain.util.time.*
import app.presentation.theme.*
import kotlin.math.*
import kotlin.random.*

@Composable
fun DataChart(
    modifier: Modifier,
    chartTimeData: List<ChartTimeData>,
    chartSettings: ChartSettings
) {
    if (chartTimeData.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()

    var chosenChartTimeData by remember { mutableStateOf(chartTimeData[0]) }
    val timeText by remember(chosenChartTimeData) {
        mutableStateOf((chosenChartTimeData.title?.let { "$it by " } ?: "") + when (chosenChartTimeData) {
            is ChartTimeData.Day -> "Days"
            is ChartTimeData.Hour -> "Hours"
            is ChartTimeData.Month -> "Months"
            is ChartTimeData.Week -> "Weeks"
            is ChartTimeData.Year -> "Years"
        })
    }

    Column(
        modifier = modifier.border(1.dp, Color.DarkGray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        layout(placeable.height, placeable.width) {
                            placeable.place(
                                x = -(placeable.width / 2 - placeable.height / 2),
                                y = -(placeable.height / 2 - placeable.width / 2)
                            )
                        }
                    }
                    .rotate(-90f),
                text = chartSettings.yTitle,
                style = chartSettings.titleStyle,
                color = chartSettings.textColor
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = timeText,
                    style = chartSettings.titleStyle,
                    color = chartSettings.textColor
                )
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 4.dp, top = 4.dp)
                ) {
                    drawChartBackground(
                        chartSettings = chartSettings
                    )
                    drawDataOnChart(
                        textMeasurer = textMeasurer,
                        data = chosenChartTimeData.data,
                        chartSettings = chartSettings
                    )
                }
            }
        }
        if (chartTimeData.size > 1) {
            Spacer(modifier = Modifier.height(3.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = MaterialTheme.colorScheme.onBackground,
                thickness = 1.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                chartTimeData.forEachIndexed { index, timeData ->
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                onClick = {
                                    chosenChartTimeData = timeData
                                }
                            )
                            .padding(8.dp),
                        text = when (timeData) {
                            is ChartTimeData.Day -> "Days"
                            is ChartTimeData.Hour -> "Hours"
                            is ChartTimeData.Month -> "Months"
                            is ChartTimeData.Week -> "Weeks"
                            is ChartTimeData.Year -> "Years"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textDecoration = if (chosenChartTimeData::class == timeData::class) TextDecoration.Underline else TextDecoration.None,
                        textAlign = TextAlign.Center
                    )
                    if (index < chartTimeData.size - 1) {
                        Divider(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight(),
                            color = MaterialTheme.colorScheme.onBackground,
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DataChartPreview() {
    val random = Random(System.currentTimeMillis())

    AppTheme(Theme.LIGHT) {
        Box(
            modifier = Modifier
                .size(600.dp, 400.dp)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            DataChart(
                modifier = Modifier.fillMaxSize(1f),
                chartTimeData = listOf(
                    ChartTimeData.Hour(
                        buildList {
                            for (i in 0..30) {
                                add(random.nextInt(30))
                            }
                        }
                    )
                ),
                chartSettings = defaultChartSettings()
            )
        }
    }
}

fun DrawScope.drawChartBackground(chartSettings: ChartSettings) {
    drawRect(
        color = chartSettings.backgroundColor,
        topLeft = Offset(x = 0f, y = 0f),
        size = size
    )

    drawLine(
        color = chartSettings.linesColor,
        start = Offset(0f, size.height * 0.95f),
        end =  Offset(size.width, size.height * 0.95f),
        strokeWidth = 2f
    )
    drawLine(
        color = chartSettings.linesColor,
        start = Offset(size.width * 0.05f, 0f),
        end =  Offset(size.width * 0.05f, size.height),
        strokeWidth = 2f
    )
}

fun DrawScope.drawDataOnChart(
    data: List<Int>,
    textMeasurer: TextMeasurer,
    chartSettings: ChartSettings
) {
    val min = if (data.any { it < 0 }) {
        data.minOrNull() ?: return
    } else {
        0
    }
    val max = data.maxOrNull() ?: return
    val height = max - min
    val width = data.size

    val drawnLines = mutableListOf<Float>()
    val drawnTexts = mutableListOf<Int>()

    val step = 1 + height / 20

    for (index in min..max) {
        val entryHeight = index.toFloat() - min

        if (!drawnLines.contains(entryHeight) && entryHeight.roundToInt() % step == 0) {
            val point = Offset(
                x = 0f,
                y = size.height * 0.925f - entryHeight / height * size.height * 0.9f
            )

            drawLine(
                color = chartSettings.linesColor,
                start = point.copy(x = size.width * 0.04f),
                end = point.copy(x = size.width),
                strokeWidth = 0.5f
            )
            drawnLines.add(entryHeight)
        }
    }

    if (!drawnLines.contains(max - min.toFloat())) {
        val point = Offset(
            x = 0f,
            y = size.height * 0.925f - (max - min) / height * size.height * 0.9f
        )

        drawLine(
            color = chartSettings.linesColor,
            start = point.copy(x = size.width * 0.04f),
            end = point.copy(x = size.width),
            strokeWidth = 0.5f
        )
    }

    drawnLines.clear()

    data.forEachIndexed { index, entry ->
        val entryHeight = min - entry.toFloat()
        val entryWidth = index.toFloat()

        val point = Offset(
            x = size.width * 0.075f + entryWidth / width * size.width * 0.9f,
            y = size.height * 0.925f + entryHeight / height * size.height * 0.9f
        )

        drawRect(
            color = chartSettings.dataColor,
            topLeft = point.copy(x = point.x),
            size = Size(width = 1f / width * size.width * 0.9f, height = size.height * 0.95f - point.y)
        )
        drawRect(
            color = Color.Black,
            topLeft = point.copy(x = point.x),
            size = Size(width = 1f / width * size.width * 0.9f, height = size.height * 0.95f - point.y),
            style = Stroke(0.5f)
        )

        if (!drawnLines.contains(entryHeight)) drawnLines.add(entryHeight)

        if (!drawnTexts.contains(entry)) {
            val textLayoutResult = textMeasurer.measure(
                text = entry.toString(),
                style = TextStyle.Default.copy(
                    fontSize = 8.sp,
                    textAlign = TextAlign.End
                )
            )
            val textPoint = point.copy(x = size.width * 0.025f)
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = textPoint.copy(
                    x = textPoint.x - textLayoutResult.size.width / 2,
                    y = textPoint.y - textLayoutResult.size.height / 2
                ),
                color = chartSettings.textColor
            )
            drawnTexts.add(entry)
        }

        val textLayoutResult = textMeasurer.measure(
            text = index.toString(),
            style = TextStyle.Default.copy(
                fontSize = 8.sp
            )
        )
        val textPoint = point.copy(x = point.x + 0.5f / width * size.width * 0.9f, y = size.height * 0.975f)
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = textPoint.copy(
                x = textPoint.x - textLayoutResult.size.width / 2,
                y = textPoint.y - textLayoutResult.size.height / 2
            ),
            color = chartSettings.textColor
        )
    }
}