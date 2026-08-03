package com.alpaomega1136.statsdroid.ui.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize

object StatsMotion {
    const val FastDurationMs = 180
    const val MediumDurationMs = 300
    const val ChartRevealDurationMs = 500

    val FastFloatSpec: FiniteAnimationSpec<Float> = tween(durationMillis = FastDurationMs, easing = FastOutSlowInEasing)
    val FastColorSpec: AnimationSpec<Color> = tween(durationMillis = FastDurationMs, easing = FastOutSlowInEasing)
    val FastSizeSpec: FiniteAnimationSpec<IntSize> = tween(durationMillis = FastDurationMs, easing = FastOutSlowInEasing)

    val MediumSizeSpec: FiniteAnimationSpec<IntSize> = tween(durationMillis = MediumDurationMs, easing = FastOutSlowInEasing)

    val ChartRevealFloatSpec: FiniteAnimationSpec<Float> = tween(durationMillis = ChartRevealDurationMs, easing = FastOutSlowInEasing)

    val BouncySpring = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow,
    )
}
