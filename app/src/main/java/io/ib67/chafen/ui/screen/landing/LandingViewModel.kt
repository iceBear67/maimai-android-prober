package io.ib67.chafen.ui.screen.landing

import android.util.Log
import androidx.lifecycle.ViewModel
import io.ib67.chafen.Config
import io.ib67.chafen.network.MaimaiPlayerData
import io.ib67.chafen.network.ResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

class LandingViewModel : ViewModel() {
    val ktorHttpClient = HttpClient(CIO)
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
}