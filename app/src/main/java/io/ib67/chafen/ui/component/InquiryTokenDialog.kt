package io.ib67.chafen.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Preview
@Composable
private fun PreviewInquiryTokenDialog() {
    InquiryTokenDialog {

    }
}

@Composable
fun InquiryTokenDialog(
    defaultValue: String = "",
    onRequestCompletion: (String?) -> Unit
) {
    Dialog(onDismissRequest = {
        onRequestCompletion(null)
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "使用密钥授权",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "登陆 maimai.lxns.net 后，在「账号详情」->「个人 API 密钥」 中得到 API 密钥。",
                    style = MaterialTheme.typography.bodyMedium,
                )
                var token by remember { mutableStateOf(defaultValue) }
                TextField(
                    value = token, onValueChange = { token = it.trim() },
                    label = { Text(text = "API 密钥") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        onRequestCompletion(token)
                    }) {
                        Text(text = "确认")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        onRequestCompletion(null)
                    }) {
                        Text(text = "取消")
                    }
                }
            }
        }
    }
}