package io.ib67.chafen.ui.screen.landing

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import io.ib67.chafen.ktorHttpClient
import io.ib67.chafen.network.MaimaiPlayerData
import io.ib67.chafen.network.ResponseWrapper
import io.ib67.chafen.ui.component.Stepper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.lang.IllegalArgumentException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen() {
    Scaffold(
        topBar = {
            LargeTopAppBar(title = {
                Text(text = "同步游戏数据")
            })
        }
    ) { scaffoldPaddings ->
        Column(
            modifier = Modifier
                .padding(scaffoldPaddings)
        ) {
            var loggedIn by remember { mutableStateOf(false) }
            AccountCard(loggedIn = { loggedIn = true })

            UploadCard(loggedIn)

        }
    }
}

@Composable
fun UploadCard(loggedIn: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                text = "上传分数",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column {
                var index by remember { mutableStateOf(0) }
                StepLogin(loggedIn, index = index) {
                    index++
                }
            }
        }
    }
}

@Composable
fun StepLogin(loggedIn: Boolean, index: Int, increaseFunc: () -> Unit) {
    Stepper(
        index = index, title = "登陆到 LxNet", status = loggedIn,
        nextStepper = { StepSelectGame(it, increaseFunc) }
    ) {

        Button(onClick = { increaseFunc() }, enabled = loggedIn) {
            Text(text = "下一步")
        }


    }
}

@Composable
fun StepSelectGame(index: Int, increaseFunc: () -> Unit) {
    var selectedGame by remember { mutableStateOf("") }
    Stepper(
        index = index,
        title = "选择目标游戏",
        status = selectedGame.isNotEmpty(),
        nextStepper = { StepProxy(it, increaseFunc) }
    ) {
        Text(text = "暂时只能一次抓取一个游戏的数据。")
        ChunithmOrMaimai {
            selectedGame = it
            increaseFunc()
        }
    }
}

@Composable
fun StepProxy(index: Int, increaseFunc: () -> Unit) {
    Stepper(index = index, title = "启动代理", status = false, nextStepper = {}) {
        Text(text = "如果系统弹窗请求权限，请允许。")
        Button(onClick = {
            
        }) {
            Text(text = "Start!")
        }
    }
}


@Composable
fun ChunithmOrMaimai(
    selectGame: (String) -> Unit
) {
    var selectedGame by remember { mutableStateOf("") }
    Row(
        modifier = Modifier.padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (selectedGame == "chunithm") {
            Button(onClick = {
                selectedGame = "chunithm"
                selectGame(selectedGame)
            }) {
                Text(text = "中二节奏")
            }
        } else {
            OutlinedButton(onClick = {
                selectedGame = "chunithm"
                selectGame(selectedGame)
            }) {
                Text(text = "中二节奏")
            }
        }
        if (selectedGame == "maimai") {
            Button(onClick = {
                selectedGame = "maimai"
                selectGame(selectedGame)
            }) {
                Text(text = "舞萌 DX")
            }
        } else {
            OutlinedButton(onClick = {
                selectedGame = "maimai"
                selectGame(selectedGame)
            }) {
                Text(text = "舞萌 DX")
            }
        }
    }
}

@Composable
fun AccountCard(loggedIn: @Composable () -> Unit) {
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
            var authorizedState by remember {
                mutableStateOf(0)
            }

            when (authorizedState) {
                0 -> {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "正在检测登录状态",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                1 -> {
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
                                    text = "${Config.userName}",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(text = "已登入")

                            }
                        }
                        loggedIn()
                    }
                }

                2 -> {
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
                        var _token by remember { mutableStateOf(Config.userToken) }
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
                            Config.userToken = _token
                            authorizedState = 0
                        }) {
                            Text(text = "登陆")
                        }
                    }

                }
            }
            val context = LocalContext.current
            LaunchedEffect(key1 = authorizedState) {
                coroutineScope.launch {
                    runCatching {
                        validateToken(Config.userToken) {
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

val LocalJson = Json {
    ignoreUnknownKeys = true
}

suspend fun validateToken(userToken: String, complete: (Int) -> Unit) {
    val resp = ktorHttpClient.get("https://maimai.lxns.net/api/v0/user/maimai/player") {
        header("X-User-Token", userToken)
        header("User-Agent", "maimai-prober-android(LxnsNB Edition)/0.1.0")
    }
    if (resp.status == HttpStatusCode.OK) {
        val body = resp.bodyAsText()
        val wrapped = LocalJson.decodeFromString<ResponseWrapper<MaimaiPlayerData>>(body)
        if (!wrapped.success) {
            // what??
            Log.e("TokenValidator", "$body")
            throw IllegalArgumentException("登录失败，请检查输入内容")
        } else {
            Config.userName = wrapped.data.name
            complete(1)
        }
    } else {
        throw IllegalArgumentException("登录失败，请检查输入内容")

    }
}
