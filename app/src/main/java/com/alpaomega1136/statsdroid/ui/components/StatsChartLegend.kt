package com.alpaomega1136.statsdroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

data class LegendItem(
    val label: String,
    val color: Color,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatsChartLegend(
    items: List<LegendItem>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.padding(vertical = StatsSpacing.Small),
        horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
        verticalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
    ) {
        items.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(item.color),
                )
                Spacer(modifier = Modifier.width(StatsSpacing.Small))
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
