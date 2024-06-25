package io.ib67.chafen.ui.screen.landing

import android.util.Log
import androidx.lifecycle.ViewModel
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

    suspend fun validateToken(userToken: String, complete: (String) -> Unit) {
        val resp = ktorHttpClient.get("https://maimai.lxns.net/api/v0/user/maimai/player") {
            header("X-User-Token", userToken)
            header("User-Agent", "maimai-prober-android(LxnsNB Edition)/0.1.0")
        }
        if (resp.status == HttpStatusCode.OK) {
            val body = resp.bodyAsText()
            val wrapped = LocalJson.decodeFromString<ResponseWrapper<MaimaiPlayerData>>(body)
            if (!wrapped.success) {
                // exception!
                Log.e("TokenValidator", body)
                throw IllegalArgumentException("未知错误?? (#01)")
            } else {
                complete(wrapped.data.name)
            }
        } else {
            throw IllegalArgumentException("错误的密钥，请检查输入内容")

        }
    }
}