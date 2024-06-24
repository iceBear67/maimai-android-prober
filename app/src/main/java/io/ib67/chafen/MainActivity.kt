package io.ib67.chafen

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.tencent.mmkv.MMKV
import io.ib67.chafen.ui.screen.landing.LandingScreen
import io.ib67.chafen.ui.theme.ChafenTheme

val ProxyProvider = compositionLocalOf<() -> Unit> { error("no provider present") }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MMKV.initialize(this)
        setContent {
            ChafenTheme {
                CompositionLocalProvider(ProxyProvider provides { startProxy() }) {
                    LandingScreen()
                }
            }
        }
    }

    fun startProxy() {
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        if (!notificationManager.areNotificationsEnabled()) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivity(intent)
        } else {
            val intent = Intent(this, ProxyActivity::class.java)
            startActivity(intent)
        }
    }
}