package io.ib67.chafen.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable

@Composable
fun ConditionalOutlineButton(
    condition: Boolean,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    if (condition) {
        Button(onClick = onClick, content = content)
    } else {
        OutlinedButton(onClick = onClick, content = content)
    }
}