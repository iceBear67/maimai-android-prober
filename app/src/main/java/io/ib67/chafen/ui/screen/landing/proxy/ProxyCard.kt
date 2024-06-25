package io.ib67.chafen.ui.screen.landing.proxy

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.ib67.chafen.ProxyViewModel
import io.ib67.chafen.ui.component.CopiedLink
import io.ib67.chafen.ui.component.GameChip
import io.ib67.chafen.ui.component.Stepper

private val VM = compositionLocalOf<ProxyViewModel> { error("no model :(") }

@Composable
fun ProxyCard(proxyModel: ProxyViewModel) {
    val i by proxyModel.step.collectAsState()
    CompositionLocalProvider(VM provides proxyModel) {
        Column {
            StepChooseGame(index = i.value) { proxyModel.step() }
        }
    }
}

@Composable
private fun StepChooseGame(index: Int, inc: () -> Unit) {
    var chosenGame by remember { mutableStateOf<List<String>>(emptyList()) }
    Stepper(index = index, title = "选择游戏", status = chosenGame.isNotEmpty(), nextStepper = {
        StepOpenProxy(index = it, inc = inc)
    }) {
        Text(
            text = """
                    |选择需要爬取的游戏，然后点击开始。
                    |
                    |开始后，查分器 APP 将会在您的粘贴板中放入一串链接。在微信中打开此链接后，查分器将在后台自动下载分数数据并且上传到服务器。
                    """.trimMargin(),
            style = MaterialTheme.typography.bodyMedium
        )
        val selectedGames = remember {
            mutableStateListOf<String>()
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GameChip(
                selectedGames = selectedGames,
                name = "maimai",
                label = "舞萌 DX",
                enabled = chosenGame.isEmpty()
            )
            GameChip(
                selectedGames = selectedGames,
                name = "chunithm",
                label = "中二节奏",
                enabled = chosenGame.isEmpty()
            )
        }
        Text(
            text = "此过程需要通知及 VPN 权限，为了确保 APP 正常运行，请批准可能的权限申请请求。",
            color = MaterialTheme.colorScheme.secondary
        )
        Button(
            enabled = chosenGame.isEmpty(),
            onClick = {
                if (selectedGames.isNotEmpty()) {
                    chosenGame = selectedGames
                    inc()
                }
            }) {
            Text(text = "开始")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun StepOpenProxy(index: Int, inc: () -> Unit) {
    var started by remember {
        mutableStateOf(false)
    }
    val vm = VM.current
    Stepper(index = index, title = "启动代理服务", status = started, nextStepper = {
        StepAwaitCookie(it, inc)
    }) {
        val notificationPermState = rememberPermissionState(
            android.Manifest.permission.POST_NOTIFICATIONS
        )
        val context = LocalContext.current
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "需要授权。", style = MaterialTheme.typography.bodyMedium)
            Button(onClick = {
                if (!notificationPermState.status.isGranted) {
                    notificationPermState.launchPermissionRequest()
                    return@Button
                }
                if (notificationPermState.status.isGranted) {
                    vm.startService(context) {
                        started = true
                        inc()
                    }
                } else {
                    Toast.makeText(context, "需要授权通知...", Toast.LENGTH_LONG).apply { show() }
                }
            },
                enabled = !started
            ) {
                Text(text = "启动服务")
            }
        }
    }
}

@Composable
private fun StepAwaitCookie(index: Int, inc: () -> Unit) {
    val cookie by VM.current.cookieCaptured.collectAsState()
    Stepper(
        index = index,
        title = "在微信中打开链接",
        status = cookie.value.isNotEmpty(),
        nextStepper = { StepCrawling(index = it) }
    ) {
        Text(text = "复制以下链接后，您可以在微信的搜索框内搜索该链接 -> 打开网址 或 发送给自己后在聊天页面内点击。总之，使用微信浏览器打开即可。")
        Spacer(modifier = Modifier.height(8.dp))
        CopiedLink(link = "http://localhost:8233")
        if (cookie.value.isNotEmpty()) {
            inc()
        }
    }
}

@Composable
private fun StepCrawling(index: Int) {
    Stepper(index = index, title = "完成！", status = true, nextStepper = {}) {
        Text(text = "APP 正在后台下载数据，稍后您将会收到一条关于上传结果的通知。")
    }
}