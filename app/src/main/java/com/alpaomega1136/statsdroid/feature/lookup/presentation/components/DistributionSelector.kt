package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alpaomega1136.statsdroid.feature.lookup.presentation.DistributionType
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.components.StatsSegmentedControl

@Composable
fun DistributionSelector(
    selectedDistribution: DistributionType,
    onDistributionSelected: (DistributionType) -> Unit,
    modifier: Modifier = Modifier,
) {
    StatsSectionCard(
        title = "Choose a distribution",
        modifier = modifier,
    ) {
        StatsSegmentedControl(
            items = DistributionType.entries,
            selectedItem = selectedDistribution,
            onItemSelected = onDistributionSelected,
            itemLabel = { it.displayName },
        )
    }
}
