package com.alpaomega1136.statsdroid.feature.clt.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.core.statistics.model.DensityCurvePoint
import com.alpaomega1136.statsdroid.core.statistics.model.HistogramData
import com.alpaomega1136.statsdroid.ui.components.LegendItem
import com.alpaomega1136.statsdroid.ui.components.StatsChartLegend
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing
import java.util.Locale

@Composable
fun CltHistogramChart(
    title: String,
    description: String,
    histogram: HistogramData,
    modifier: Modifier = Modifier,
    theoreticalCurve: List<DensityCurvePoint> = emptyList(),
) {
    val barColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
    val curveColor = MaterialTheme.colorScheme.tertiary
    val axisColor = MaterialTheme.colorScheme.outline

    var isChartVisible by remember { mutableStateOf(false) }

    LaunchedEffect(histogram) {
        isChartVisible = false
        isChartVisible = true
    }

    val animationProgress by animateFloatAsState(
        targetValue = if (isChartVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "histogram_reveal",
    )

    StatsSectionCard(
        title = title,
        subtitle = description,
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(StatsSpacing.Small)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
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
                    val fullBarHeight = (baselineY - yCoordinate(bin.density)).coerceAtLeast(0f)
                    val animatedHeight = fullBarHeight * animationProgress
                    val width = (binWidthInPixels - gap).coerceAtLeast(1f)

                    drawRect(
                        color = barColor,
                        topLeft = Offset(
                            x = leftPadding + index * binWidthInPixels + gap / 2f,
                            y = baselineY - animatedHeight,
                        ),
                        size = Size(width = width, height = animatedHeight),
                    )
                }

                val visibleCurvePoints = theoreticalCurve.filter { it.x in histogram.range.minimum..histogram.range.maximum }
                if (visibleCurvePoints.size >= 2 && animationProgress > 0.3f) {
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

            val legendItems = mutableListOf(
                LegendItem(label = "Empirical density", color = barColor),
            )
            if (theoreticalCurve.isNotEmpty()) {
                legendItems.add(LegendItem(label = "Theoretical normal curve", color = curveColor))
            }
            StatsChartLegend(items = legendItems)

            Text(
                text = "Observations: ${histogram.totalCount} | Bins: ${histogram.bins.size}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatAxisValue(value: Double): String = String.format(Locale.US, "%.2f", value)
