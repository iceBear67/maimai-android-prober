package io.ib67.chafen.ui.screen.landing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.ib67.chafen.ProxyProvider
import io.ib67.chafen.ui.component.AccountCard
import io.ib67.chafen.ui.component.Stepper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    viewModel: LandingViewModel = viewModel()
) {
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
            AccountCard(viewModel, loggedIn = { loggedIn = true })

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
        var increased by remember { mutableStateOf(false) }
        if (loggedIn && !increased) {
            increased = true
            increaseFunc()
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
        nextStepper = { StepProxy(it) }
    ) {
        Text(text = "暂时只能一次抓取一个游戏的数据。")
        ChunithmOrMaimai {
            selectedGame = it
            increaseFunc()
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StepProxy(index: Int) {
    val startFunc = ProxyProvider.current
    Stepper(index = index, title = "启动代理", status = false, nextStepper = {}) {
        val notificationPermState = rememberPermissionState(
            android.Manifest.permission.POST_NOTIFICATIONS //todo: support <13
        )
        if (notificationPermState.status.isGranted) {
            Text(text = "此过程可能需要授权 VPN 权限。")
            Button(
                onClick = startFunc
            ) {
                Text(text = "Start!")
            }
        } else {
            Text(text = "请先授权 APP 通知权限。此权限仅用于辅助您进行后续操作及通知您上传进度")
            Button(
                onClick = {
                    notificationPermState.launchPermissionRequest()
                }) {
                Text(text = "授权")
            }
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
        ConditionalOutlineButton(condition = selectedGame == "chunithm", onClick = {
            selectedGame = "chunithm"
            selectGame(selectedGame)
        }) {
            Text(text = "中二节奏")
        }
        ConditionalOutlineButton(condition = selectedGame == "maimai", onClick = {
            selectedGame = "maimai"
            selectGame(selectedGame)
        }) {
            Text(text = "舞萌 DX")
        }
    }
}

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