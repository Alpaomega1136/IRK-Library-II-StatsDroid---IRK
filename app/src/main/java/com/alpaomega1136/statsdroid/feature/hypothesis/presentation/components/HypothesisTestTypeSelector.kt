package com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType

@Composable
fun HypothesisTestTypeSelector(
    selectedTestType: HypothesisTestType,
    onTestTypeSelected: (HypothesisTestType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Statistical Test", style = MaterialTheme.typography.titleMedium)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HypothesisTestType.entries.forEach { testType ->
                FilterChip(
                    selected = selectedTestType == testType,
                    onClick = { onTestTypeSelected(testType) },
                    label = { Text(text = testType.displayName) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
