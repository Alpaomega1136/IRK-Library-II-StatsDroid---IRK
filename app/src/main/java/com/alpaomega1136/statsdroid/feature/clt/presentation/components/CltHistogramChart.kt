package com.alpaomega1136.statsdroid.feature.clt.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.core.statistics.model.DensityCurvePoint
import com.alpaomega1136.statsdroid.core.statistics.model.HistogramData
import java.util.Locale

@Composable
fun CltHistogramChart(
    title: String,
    description: String,
    histogram: HistogramData,
    modifier: Modifier = Modifier,
    theoreticalCurve: List<DensityCurvePoint> = emptyList(),
) {
    val barColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
    val curveColor = MaterialTheme.colorScheme.error
    val axisColor = MaterialTheme.colorScheme.outline

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .semantics {
                        contentDescription = "$title. Contains ${histogram.totalCount} observations across ${histogram.bins.size} bins."
                    },
            ) {
                if (histogram.bins.isEmpty()) return@Canvas

                val leftPadding = 12.dp.toPx()
                val rightPadding = 12.dp.toPx()
                val topPadding = 12.dp.toPx()
                val bottomPadding = 20.dp.toPx()
                val baselineY = size.height - bottomPadding
                val graphWidth = size.width - leftPadding - rightPadding
                val graphHeight = baselineY - topPadding
                val curveMaximumDensity = theoreticalCurve.maxOfOrNull { it.density } ?: 0.0
                val maximumDensity = maxOf(histogram.maximumDensity, curveMaximumDensity)

                if (!maximumDensity.isFinite() || maximumDensity <= 0.0) return@Canvas

                fun xCoordinate(value: Double): Float {
                    val fraction = (value - histogram.range.minimum) / (histogram.range.maximum - histogram.range.minimum)
                    return leftPadding + fraction.toFloat() * graphWidth
                }

                fun yCoordinate(density: Double): Float {
                    return baselineY - (density / maximumDensity).toFloat() * graphHeight
                }

                drawLine(
                    color = axisColor,
                    start = Offset(leftPadding, baselineY),
                    end = Offset(size.width - rightPadding, baselineY),
                    strokeWidth = 1.dp.toPx(),
                )

                val binWidthInPixels = graphWidth / histogram.bins.size.toFloat()
                val gap = minOf(2.dp.toPx(), binWidthInPixels * 0.12f)

                histogram.bins.forEachIndexed { index, bin ->
                    val barHeight = (baselineY - yCoordinate(bin.density)).coerceAtLeast(0f)
                    val width = (binWidthInPixels - gap).coerceAtLeast(1f)
                    drawRect(
                        color = barColor,
                        topLeft = Offset(
                            x = leftPadding + index * binWidthInPixels + gap / 2f,
                            y = baselineY - barHeight,
                        ),
                        size = Size(width = width, height = barHeight),
                    )
                }

                val visibleCurvePoints = theoreticalCurve.filter { it.x in histogram.range.minimum..histogram.range.maximum }
                if (visibleCurvePoints.size >= 2) {
                    val curvePath = Path().apply {
                        visibleCurvePoints.forEachIndexed { index, point ->
                            val x = xCoordinate(point.x)
                            val y = yCoordinate(point.density)
                            if (index == 0) moveTo(x, y) else lineTo(x, y)
                        }
                    }
                    drawPath(path = curvePath, color = curveColor, style = Stroke(width = 3.dp.toPx()))
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = formatAxisValue(histogram.range.minimum), style = MaterialTheme.typography.labelSmall)
                Text(text = formatAxisValue((histogram.range.minimum + histogram.range.maximum) / 2.0), style = MaterialTheme.typography.labelSmall)
                Text(text = formatAxisValue(histogram.range.maximum), style = MaterialTheme.typography.labelSmall)
            }

            HistogramLegend(
                barColor = barColor,
                curveColor = curveColor,
                showTheoreticalCurve = theoreticalCurve.isNotEmpty(),
            )

            Text(
                text = "Observations: ${histogram.totalCount} | Bins: ${histogram.bins.size}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HistogramLegend(
    barColor: Color,
    curveColor: Color,
    showTheoreticalCurve: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        LegendItem(color = barColor, label = "Empirical density")
        if (showTheoreticalCurve) {
            LegendItem(color = curveColor, label = "Theoretical normal curve")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = RoundedCornerShape(2.dp)),
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

private fun formatAxisValue(value: Double): String = String.format(Locale.US, "%.2f", value)
