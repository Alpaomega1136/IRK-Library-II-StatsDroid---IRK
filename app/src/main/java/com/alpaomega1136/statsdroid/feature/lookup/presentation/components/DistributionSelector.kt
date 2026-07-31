package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.alpaomega1136.statsdroid.feature.lookup.presentation.DistributionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistributionSelector(
    selectedDistribution: DistributionType,
    onDistributionSelected: (DistributionType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = {
            isExpanded = !isExpanded
        },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selectedDistribution.displayName,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = {
                Text(text = "Distribution")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = isExpanded,
                )
            },
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true,
                )
                .fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            },
        ) {
            DistributionType.entries.forEach { distribution ->
                DropdownMenuItem(
                    text = {
                        Text(text = distribution.displayName)
                    },
                    onClick = {
                        onDistributionSelected(distribution)
                        isExpanded = false
                    },
                )
            }
        }
    }
}
