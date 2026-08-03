package com.alpaomega1136.statsdroid.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val SmallControlShape = RoundedCornerShape(12.dp)
val StandardCardShape = RoundedCornerShape(20.dp)
val ResultBannerShape = RoundedCornerShape(24.dp)
val HeroCardShape = RoundedCornerShape(28.dp)
val NavigationPillShape = RoundedCornerShape(CornerSize(50))

val StatsDroidShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = SmallControlShape,
    medium = StandardCardShape,
    large = ResultBannerShape,
    extraLarge = HeroCardShape,
)
