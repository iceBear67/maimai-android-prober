package io.ib67.chafen.ui.component

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.ib67.chafen.Config
import io.ib67.chafen.ui.screen.landing.LandingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AccountCard(
    viewModel: LandingViewModel, //todo 拆掉 viewmodel 和对 Config 的依赖
    loggedIn: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val coroutineScope = rememberCoroutineScope()
            var authorizedState by remember { mutableStateOf(0) }
            when (authorizedState) {
                0 -> CardStateChecking()
                1 -> {
                    CardStateLoggedIn(userName = Config.userName)
                    loggedIn()
                }

                2 -> {
                    CardStateInquiringToken(preCachedToken = Config.userToken) { tk ->
                        Config.userToken = tk
                        authorizedState = 0 // trigger update
                    }
                }
            }
            val context = LocalContext.current
            LaunchedEffect(key1 = authorizedState) {
                coroutineScope.launch {
                    runCatching {
                        viewModel.validateToken(Config.userToken) {
                            authorizedState = it
                        }
                    }.onFailure {
                        authorizedState = 2
                        withContext(Dispatchers.Main) {
                            it.printStackTrace()
                            Toast.makeText(context, "登录失败 ${it.message}", Toast.LENGTH_LONG)
                                .also { it.show() }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardStateInquiringToken(
    preCachedToken: String,
    updateToken: (String) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "account not logged in",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "账号未登入",
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Text(text = "注意：密钥可能过期，请重新生成。")
        var _token by remember { mutableStateOf(preCachedToken) }
        TextField(value = _token,
            label = { Text("API 密钥") },
            singleLine = true,
            onValueChange = { _token = it.trim() },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "请输入「账号详情」中的 API 密钥"
                )
            })
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            updateToken(_token)
        }) {
            Text(text = "登陆")
        }
    }
}

@Composable
fun CardStateLoggedIn(userName: String) {
    Column {
        Text(
            text = "用户信息",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "account logged in",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(text = "已登入")

            }
        }
    }
}

@Composable
fun CardStateChecking() {
    CircularProgressIndicator(modifier = Modifier.size(32.dp))
    Spacer(modifier = Modifier.width(10.dp))
    Text(
        text = "正在检测登录状态",
        style = MaterialTheme.typography.titleLarge
    )
}

