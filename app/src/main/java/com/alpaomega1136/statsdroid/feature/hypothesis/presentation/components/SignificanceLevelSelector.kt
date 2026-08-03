package com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.SignificanceLevel
import com.alpaomega1136.statsdroid.ui.components.StatsSegmentedControl
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

@Composable
fun SignificanceLevelSelector(
    selectedLevel: SignificanceLevel,
    onLevelSelected: (SignificanceLevel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Significance Level (\u03B1)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(StatsSpacing.Small))

        StatsSegmentedControl(
            items = SignificanceLevel.entries,
            selectedItem = selectedLevel,
            onItemSelected = onLevelSelected,
            itemLabel = { it.displayName },
        )
    }
}
