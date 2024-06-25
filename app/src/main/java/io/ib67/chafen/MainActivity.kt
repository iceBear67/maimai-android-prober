package io.ib67.chafen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tencent.mmkv.MMKV
import io.ib67.chafen.ui.component.BasicCard
import io.ib67.chafen.ui.screen.landing.LandingScreen
import io.ib67.chafen.ui.screen.landing.proxy.ProxyCard
import io.ib67.chafen.ui.theme.ChafenTheme


class MainActivity : ComponentActivity() {
    var launcher: ActivityResultLauncher<Intent>? = null // todo remove this shit
    private lateinit var proxyModel: ProxyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MMKV.initialize(this)
        initLauncher { }
        setContent {
            ChafenTheme {
                proxyModel = viewModel()
                LandingScreen {
                    ProxyStepperCard()
                }
            }
        }
    }

    fun initLauncher(onSuccess: () -> Unit) {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).apply { show() }
                    proxyModel.connectToService(this, onSuccess)
                } else {
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).apply { show() }
                }
            }
    }

    @Composable
    private fun ProxyStepperCard() {
        BasicCard(
            color = CardDefaults.outlinedCardColors(),
            title = {
                Text(
                    text = "使用代理上传",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = "upload",
                    modifier = Modifier.size(32.dp)
                )
            }) {
            ProxyCard(proxyModel = proxyModel)
        }
    }
}