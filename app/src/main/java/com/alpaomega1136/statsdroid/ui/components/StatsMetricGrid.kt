package com.alpaomega1136.statsdroid.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

data class StatsMetricItem(
    val label: String,
    val value: String,
    val subValue: String? = null,
    val useMonospace: Boolean = false,
    val valueColor: Color? = null,
)

@Composable
fun StatsMetricGrid(
    items: List<StatsMetricItem>,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val useTwoColumns = maxWidth >= 520.dp

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
        ) {
            if (useTwoColumns) {
                items.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
                    ) {
                        rowItems.forEach { item ->
                            MetricItem(
                                item = item,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                items.forEach { item ->
                    MetricItem(item = item)
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    item: StatsMetricItem,
    modifier: Modifier = Modifier,
) {
    StatsMetricCard(
        label = item.label,
        value = item.value,
        subValue = item.subValue,
        useMonospace = item.useMonospace,
        valueColor = item.valueColor ?: androidx.compose.material3.MaterialTheme.colorScheme.primary,
        modifier = modifier,
    )
}
