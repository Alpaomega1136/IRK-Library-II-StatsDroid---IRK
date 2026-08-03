package com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.components.StatsSegmentedControl

@Composable
fun HypothesisTestTypeSelector(
    selectedTestType: HypothesisTestType,
    onTestTypeSelected: (HypothesisTestType) -> Unit,
    modifier: Modifier = Modifier,
) {
    StatsSectionCard(
        title = "Choose a test",
        subtitle = "Use Z-Test when the population standard deviation is known; use t-Test when it is estimated from the sample.",
        modifier = modifier,
    ) {
        StatsSegmentedControl(
            items = HypothesisTestType.entries,
            selectedItem = selectedTestType,
            onItemSelected = onTestTypeSelected,
            itemLabel = { it.displayName },
        )
    }
}
