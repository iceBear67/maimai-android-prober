package io.ib67.chafen.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Stepper(
    index: Int,
    title: String,
    status: Boolean,
    nextStepper: @Composable (Int) -> Unit,
    content: @Composable () -> Unit
) {
    var visibleAnim by remember { mutableStateOf(false) }
    AnimatedVisibility(visible = visibleAnim, enter = expandVertically { -50 }) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!status) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                } else {

                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "$title is completed!"
                    )

                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 15.dp)
                )
            }
            Column(
                modifier = Modifier.padding(start = (15 + 24).dp, top = 5.dp)
            ) {
                content()
            }
        }
    }
    if (index > 0) {
        nextStepper(index - 1)
    }
    LaunchedEffect(key1 = title) {
        visibleAnim = true
    }
}