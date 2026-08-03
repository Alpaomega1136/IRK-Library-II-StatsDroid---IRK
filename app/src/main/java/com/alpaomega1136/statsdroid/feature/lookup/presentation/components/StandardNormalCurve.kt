package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import java.util.Locale
import com.alpaomega1136.statsdroid.core.statistics.model.NormalCurvePoint

@Composable
fun StandardNormalCurve(
    points: List<NormalCurvePoint>,
    selectedZScore: Double,
    modifier: Modifier = Modifier,
    onZScoreSelected: ((Double) -> Unit)? = null,
) {
    val curveColor = MaterialTheme.colorScheme.primary
    val shadedAreaColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
    val axisColor = MaterialTheme.colorScheme.outline
    val centerLineColor = MaterialTheme.colorScheme.outlineVariant
    val markerColor = MaterialTheme.colorScheme.error
    val minimumZ = points.firstOrNull()?.zScore ?: -5.0
    val maximumZ = points.lastOrNull()?.zScore ?: 5.0

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .semantics {
                    contentDescription = String.format(
                        Locale.US,
                        "Interactive standard normal curve. Tap the chart to move the z marker. Current z is %.2f.",
                        selectedZScore,
                    )
                }
                .pointerInput(minimumZ, maximumZ, onZScoreSelected) {
                    val selectionHandler = onZScoreSelected ?: return@pointerInput

                    detectTapGestures { offset ->
                        val leftPadding = 16.dp.toPx()
                        val rightPadding = 16.dp.toPx()
                        val graphWidth =
                            (size.width.toFloat() - leftPadding - rightPadding).coerceAtLeast(1f)
                        val boundedX = offset.x.coerceIn(
                            minimumValue = leftPadding,
                            maximumValue = size.width.toFloat() - rightPadding,
                        )
                        val fraction = (boundedX - leftPadding) / graphWidth
                        val selected = minimumZ + fraction * (maximumZ - minimumZ)
                        selectionHandler(selected.coerceIn(minimumZ, maximumZ))
                    }
                },
        ) {
            if (points.size < 2) return@Canvas

            val leftPadding = 16.dp.toPx()
            val rightPadding = 16.dp.toPx()
            val topPadding = 16.dp.toPx()
            val bottomPadding = 20.dp.toPx()
            val graphWidth = size.width - leftPadding - rightPadding
            val baselineY = size.height - bottomPadding
            val graphHeight = baselineY - topPadding
            val maximumDensity = points.maxOf { point -> point.density }

            fun xCoordinate(zScore: Double): Float {
                val fraction = (zScore - minimumZ) / (maximumZ - minimumZ)
                return leftPadding + fraction.toFloat() * graphWidth
            }

            fun yCoordinate(density: Double): Float {
                val fraction = density / maximumDensity
                return baselineY - fraction.toFloat() * graphHeight
            }

            drawLine(
                color = axisColor,
                start = Offset(x = leftPadding, y = baselineY),
                end = Offset(x = size.width - rightPadding, y = baselineY),
                strokeWidth = 1.dp.toPx(),
            )

            if (0.0 in minimumZ..maximumZ) {
                val centerX = xCoordinate(0.0)
                drawLine(
                    color = centerLineColor,
                    start = Offset(x = centerX, y = topPadding),
                    end = Offset(x = centerX, y = baselineY),
                    strokeWidth = 1.dp.toPx(),
                )
            }

            val boundedSelectedZ = selectedZScore.coerceIn(minimumZ, maximumZ)
            val upperPointIndex = points.indexOfFirst { point -> point.zScore >= boundedSelectedZ }
            val selectedDensity = when {
                upperPointIndex <= 0 -> points.first().density
                upperPointIndex == -1 -> points.last().density
                else -> {
                    val lowerPoint = points[upperPointIndex - 1]
                    val upperPoint = points[upperPointIndex]
                    val fraction = (boundedSelectedZ - lowerPoint.zScore) /
                        (upperPoint.zScore - lowerPoint.zScore)
                    lowerPoint.density + fraction * (upperPoint.density - lowerPoint.density)
                }
            }

            val shadedPoints = points
                .takeWhile { point -> point.zScore < boundedSelectedZ }
                .toMutableList()
                .apply {
                    add(NormalCurvePoint(zScore = boundedSelectedZ, density = selectedDensity))
                }

            val shadedPath = Path().apply {
                moveTo(x = xCoordinate(minimumZ), y = baselineY)
                shadedPoints.forEach { point ->
                    lineTo(x = xCoordinate(point.zScore), y = yCoordinate(point.density))
                }
                lineTo(x = xCoordinate(boundedSelectedZ), y = baselineY)
                close()
            }

            drawPath(path = shadedPath, color = shadedAreaColor)

            val curvePath = Path().apply {
                points.forEachIndexed { index, point ->
                    val x = xCoordinate(point.zScore)
                    val y = yCoordinate(point.density)
                    if (index == 0) moveTo(x = x, y = y) else lineTo(x = x, y = y)
                }
            }

            drawPath(
                path = curvePath,
                color = curveColor,
                style = Stroke(width = 3.dp.toPx()),
            )

            val markerX = xCoordinate(boundedSelectedZ)
            val markerY = yCoordinate(selectedDensity)

            drawLine(
                color = markerColor,
                start = Offset(x = markerX, y = markerY),
                end = Offset(x = markerX, y = baselineY),
                strokeWidth = 2.dp.toPx(),
            )
            drawCircle(
                color = markerColor,
                radius = 5.dp.toPx(),
                center = Offset(x = markerX, y = markerY),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "-5", style = MaterialTheme.typography.labelSmall)
            Text(text = "Tap chart to select z", style = MaterialTheme.typography.labelSmall)
            Text(text = "5", style = MaterialTheme.typography.labelSmall)
        }
    }
}
