package com.alpaomega1136.statsdroid.feature.clt.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.clt.domain.model.PopulationShape

@Composable
fun CltPopulationSelector(
    selectedShape: PopulationShape,
    onShapeSelected: (PopulationShape) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Population Shape", style = MaterialTheme.typography.titleMedium)

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { isExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "${selectedShape.displayName} v")
            }
            DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                PopulationShape.entries.forEach { shape ->
                    DropdownMenuItem(
                        text = { Text(text = shape.displayName) },
                        onClick = {
                            onShapeSelected(shape)
                            isExpanded = false
                        },
                    )
                }
            }
        }

        Text(
            text = selectedShape.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
