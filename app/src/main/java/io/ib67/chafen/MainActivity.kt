package io.ib67.chafen

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.tencent.mmkv.MMKV
import io.ib67.chafen.ui.screen.landing.LandingScreen

import io.ib67.chafen.ui.theme.ChafenTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.headersOf

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