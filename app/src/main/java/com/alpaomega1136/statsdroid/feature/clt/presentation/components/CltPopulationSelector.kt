package com.alpaomega1136.statsdroid.feature.clt.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import com.alpaomega1136.statsdroid.ui.theme.SmallControlShape

@Composable
fun CltPopulationSelector(
    selectedShape: PopulationShape,
    onShapeSelected: (PopulationShape) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Population shape",
            style = MaterialTheme.typography.titleMedium,
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                enabled = enabled,
                shape = SmallControlShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 58.dp),
            ) {
                Text(
                    text = selectedShape.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Open population options",
                )
            }

            DropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { expanded = false },
                modifier = Modifier.widthIn(
                    min = 292.dp,
                    max = 348.dp,
                ),
            ) {
                PopulationShape.entries.forEach { shape ->
                    DropdownMenuItem(
                        modifier = Modifier.defaultMinSize(minHeight = 88.dp),
                        contentPadding = PaddingValues(
                            horizontal = 20.dp,
                            vertical = 12.dp,
                        ),
                        text = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(
                                    text = shape.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Text(
                                    text = shape.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        },
                        onClick = {
                            onShapeSelected(shape)
                            expanded = false
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
