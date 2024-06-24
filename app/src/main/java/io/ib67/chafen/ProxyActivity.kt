package io.ib67.chafen

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ib67.chafen.ui.theme.ChafenTheme

class ProxyActivity : ComponentActivity() {
    private val serviceIntent
        get() = Intent(this, MaimaiVpnService::class.java)

    private lateinit var viewModel: ProxyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var success by remember {
                mutableStateOf(false)
            }
            var tries by remember { mutableStateOf(0) }
            ChafenTheme {
                viewModel = viewModel()
                val log by viewModel.log.collectAsState()
                ProxyScreen(log)
                LaunchedEffect(key1 = success, key2 = tries) {
                    if (success) {

                    } else {
                        viewModel.info("正在请求权限, 如果出现弹窗请同意")
                        prepareService()
                        startVpnService()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpnService()
    }

    private fun stopVpnService() {
        viewModel.info("正在停止服务")
        startService(serviceIntent.setAction(MaimaiVpnService.ACTION_DISCONNECT))
    }

    private fun startVpnService() {
        viewModel.info("正在启动服务")
        startService(serviceIntent.setAction(MaimaiVpnService.ACTION_CONNECT))
    }

    private fun prepareService() =
        VpnService.prepare(this)?.also { startActivityForResult(it, 0) }
            ?: onActivityResult(0, RESULT_OK, null)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            viewModel.info("授权成功")
            startService(serviceIntent.setAction(MaimaiVpnService.ACTION_CONNECT))
        } else {
            finish()
        }
    }

    /**
     * 这里和 Activity 糊在一起了 方便整
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ProxyScreen(
        log: List<String>
    ) {
        Scaffold(
            topBar = {
                LargeTopAppBar(title = { Text(text = "代理日志") })
            }
        ) { scaffoldPaddings ->
            Column(modifier = Modifier.padding(scaffoldPaddings))
            {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "等待数据接入",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                InstructionCard()
                LogCard(log)
            }
        }
    }

    @Composable
    private fun InstructionCard() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            val index by viewModel.step.collectAsState()

        }
    }

    @Composable
    private fun LogCard(log: List<String>) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                items(log) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}