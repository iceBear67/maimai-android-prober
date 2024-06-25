package io.ib67.chafen.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview
@Composable
fun PreviewUserCard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SimpleUserCard(userName = "icybear") {}
        NotLoggedInCard("") {

        }
        CheckingCard()
    }
}

@Composable
fun BasicCard(
    color: CardColors,
    title: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        colors = color
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()
                title()
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }

    }
}

@Composable
fun SimpleUserCard(
    userName: String,
    logout: () -> Unit
) {
    BasicCard(
        color = getCardColor(true),
        title = {
            Text(
                text = "$userName",
                style = MaterialTheme.typography.titleLarge,
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.AccountCircle, contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    ) {
        Text(text = "如果这不是您的账号，请重新登录。")
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = logout) {
            Text(text = "重新登录")
        }

    }
}

@Composable
fun CheckingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "登陆中",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Text(text = "如果登陆密钥错误也会显示账号未登录。")
        }
    }
}

@Composable
fun NotLoggedInCard(
    defaultToken: String,
    login: (String?) -> Unit
) {
    BasicCard(
        color = getCardColor(false),
        title = {
            Text(
                text = "账号未登录",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Warning, contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
    ) {
        Text(
            text = """
            请登录查分器网站，在「账号详情」 -> 「个人 API 密钥中」获取登陆密钥。
        """.trimIndent(),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            var showDialog by remember { mutableStateOf(false) }
            TextButton(
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                onClick = {
                    showDialog = true
                }) {
                Text(text = "登陆到 LxNet")
            }
            if (showDialog) {
                InquiryTokenDialog(defaultToken) {
                    showDialog = false
                    login(it)
                }
            }
        }
    }
}

@Composable
fun getCardColor(enable: Boolean): CardColors {
    return if (enable) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    }
}
