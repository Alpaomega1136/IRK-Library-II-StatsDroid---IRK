package com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TailType

@Composable
fun TailTypeSelector(
    selectedTailType: TailType,
    onTailTypeSelected: (TailType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "Alternative Hypothesis (H1)", style = MaterialTheme.typography.titleMedium)

        TailType.entries.forEach { tailType ->
            val isSelected = selectedTailType == tailType
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelected,
                        onClick = { onTailTypeSelected(tailType) },
                        role = Role.RadioButton,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = isSelected, onClick = null)
                Column {
                    Text(text = tailType.displayName)
                    Text(text = "H1: mu ${tailType.alternativeSymbol} mu0", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
