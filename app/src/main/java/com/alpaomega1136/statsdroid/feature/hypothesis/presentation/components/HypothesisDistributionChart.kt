package com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestResult
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.visualization.CurveInterval
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.visualization.DistributionCurvePoint
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.visualization.HypothesisVisualizationData
import java.util.Locale

@Composable
fun HypothesisDistributionChart(
    result: HypothesisTestResult,
    visualization: HypothesisVisualizationData,
    modifier: Modifier = Modifier,
) {
    val curveColor = MaterialTheme.colorScheme.primary
    val rejectionColor = MaterialTheme.colorScheme.error.copy(alpha = 0.30f)
    val pValueColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.36f)
    val criticalLineColor = MaterialTheme.colorScheme.error
    val statisticLineColor = MaterialTheme.colorScheme.tertiary
    val axisColor = MaterialTheme.colorScheme.outline

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Distribution under H0", style = MaterialTheme.typography.titleMedium)
            Text(
                text = when (result.testType) {
                    HypothesisTestType.Z_TEST -> "Standard normal distribution"
                    HypothesisTestType.T_TEST -> "Student's t-distribution, df = ${result.degreesOfFreedom}"
                },
                style = MaterialTheme.typography.bodyMedium,
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
            ) {
                val points = visualization.curvePoints
                if (points.size < 2) return@Canvas

                val leftPadding = 12.dp.toPx()
                val rightPadding = 12.dp.toPx()
                val topPadding = 12.dp.toPx()
                val bottomPadding = 24.dp.toPx()
                val baselineY = size.height - bottomPadding
                val graphWidth = size.width - leftPadding - rightPadding
                val graphHeight = baselineY - topPadding
                val minimumStatistic = visualization.minimumStatistic
                val maximumStatistic = visualization.maximumStatistic
                val maximumDensity = points.maxOf { it.density }.coerceAtLeast(0.000001)

                fun xCoordinate(statistic: Double): Float {
                    val fraction = (statistic - minimumStatistic) / (maximumStatistic - minimumStatistic)
                    return (leftPadding + fraction * graphWidth).toFloat()
                }

                fun yCoordinate(density: Double): Float {
                    return (baselineY - density / maximumDensity * graphHeight).toFloat()
                }

                fun densityAt(statistic: Double): Double {
                    val boundedStatistic = statistic.coerceIn(minimumStatistic, maximumStatistic)
                    val upperIndex = points.indexOfFirst { it.statistic >= boundedStatistic }.takeIf { it >= 0 } ?: points.lastIndex
                    val lowerPoint = points[(upperIndex - 1).coerceAtLeast(0)]
                    val upperPoint = points[upperIndex]
                    val denominator = upperPoint.statistic - lowerPoint.statistic

                    return if (denominator == 0.0) {
                        lowerPoint.density
                    } else {
                        val fraction = (boundedStatistic - lowerPoint.statistic) / denominator
                        lowerPoint.density + fraction * (upperPoint.density - lowerPoint.density)
                    }
                }

                fun areaPath(interval: CurveInterval): Path? {
                    val start = interval.start.coerceIn(minimumStatistic, maximumStatistic)
                    val end = interval.end.coerceIn(minimumStatistic, maximumStatistic)
                    if (start >= end) return null

                    val selectedPoints = buildList {
                        add(DistributionCurvePoint(start, densityAt(start)))
                        points.filter { it.statistic > start && it.statistic < end }.forEach(::add)
                        add(DistributionCurvePoint(end, densityAt(end)))
                    }

                    return Path().apply {
                        moveTo(xCoordinate(start), baselineY)
                        selectedPoints.forEach { lineTo(xCoordinate(it.statistic), yCoordinate(it.density)) }
                        lineTo(xCoordinate(end), baselineY)
                        close()
                    }
                }

                drawLine(
                    color = axisColor,
                    start = Offset(leftPadding, baselineY),
                    end = Offset(size.width - rightPadding, baselineY),
                    strokeWidth = 1.dp.toPx(),
                )

                visualization.rejectionRegions.forEach { areaPath(it)?.let { path -> drawPath(path, rejectionColor) } }
                visualization.pValueRegions.forEach { areaPath(it)?.let { path -> drawPath(path, pValueColor) } }

                val curvePath = Path().apply {
                    points.forEachIndexed { index, point ->
                        val x = xCoordinate(point.statistic)
                        val y = yCoordinate(point.density)
                        if (index == 0) moveTo(x, y) else lineTo(x, y)
                    }
                }

                drawPath(path = curvePath, color = curveColor, style = Stroke(width = 3.dp.toPx()))

                listOfNotNull(result.criticalValues.lower, result.criticalValues.upper).forEach { criticalValue ->
                    val x = xCoordinate(criticalValue.coerceIn(minimumStatistic, maximumStatistic))
                    drawLine(color = criticalLineColor, start = Offset(x, topPadding), end = Offset(x, baselineY), strokeWidth = 2.dp.toPx())
                }

                val markerStatistic = visualization.displayedTestStatistic
                val markerX = xCoordinate(markerStatistic)
                val markerY = yCoordinate(densityAt(markerStatistic))
                drawLine(color = statisticLineColor, start = Offset(markerX, markerY), end = Offset(markerX, baselineY), strokeWidth = 3.dp.toPx())
                drawCircle(color = statisticLineColor, radius = 5.dp.toPx(), center = Offset(markerX, markerY))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = formatAxisValue(visualization.minimumStatistic), style = MaterialTheme.typography.labelSmall)
                Text(text = "0", style = MaterialTheme.typography.labelSmall)
                Text(text = formatAxisValue(visualization.maximumStatistic), style = MaterialTheme.typography.labelSmall)
            }

            ChartLegend(
                rejectionColor = rejectionColor,
                pValueColor = pValueColor,
                criticalLineColor = criticalLineColor,
                statisticLineColor = statisticLineColor,
            )

            Text(
                text = String.format(Locale.US, "Calculated statistic: %.4f", result.testStatistic),
                style = MaterialTheme.typography.bodyMedium,
            )

            if (visualization.isTestStatisticClamped) {
                Text(
                    text = "The statistic lies outside the displayed range, so its marker is shown at the nearest chart boundary.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun ChartLegend(
    rejectionColor: Color,
    pValueColor: Color,
    criticalLineColor: Color,
    statisticLineColor: Color,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        LegendItem(color = rejectionColor, label = "Rejection region")
        LegendItem(color = pValueColor, label = "p-value area")
        LegendItem(color = criticalLineColor, label = "Critical value")
        LegendItem(color = statisticLineColor, label = "Calculated statistic")
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
