package io.ib67.chafen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tencent.mmkv.MMKV
import io.ib67.chafen.ui.screen.landing.LandingScreen
import io.ib67.chafen.ui.theme.ChafenTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

val ktorHttpClient = HttpClient(CIO)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MMKV.initialize(this)
        setContent {
            ChafenTheme {

                // A surface container using the 'background' color from the theme
                LandingScreen()

            }
        }
    }
}