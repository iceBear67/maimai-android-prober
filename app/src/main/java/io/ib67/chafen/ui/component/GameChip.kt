package io.ib67.chafen.ui.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameChip(selectedGames: MutableList<String>, name: String, label: String, enabled: Boolean) {
    FilterChip(
        selected = selectedGames.contains(name),
        onClick = {
            if (!selectedGames.contains(name)) {
                selectedGames.add(name)
            } else {
                selectedGames.remove(name)
            }
        },
        label = { Text(text = label) },
        enabled = enabled
    )
}