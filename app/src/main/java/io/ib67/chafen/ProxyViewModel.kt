package io.ib67.chafen

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.VpnService
import android.os.IBinder
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.Date

class ProxyViewModel : ViewModel() {
    private val dateFmt = SimpleDateFormat("HH:mm:ss")
    private val logTime
        get() = "[${dateFmt.format(Date())}]"
    private var logs = mutableStateListOf("$logTime 等待日志中")
    var log = MutableStateFlow(logs)
    private var _step = mutableStateOf(0)
    var step = MutableStateFlow(_step)

    private var _cookieCaptured = mutableStateOf("")
    val cookieCaptured = MutableStateFlow(_cookieCaptured)

    fun info(message: String) {
        logs.add("$logTime $message")
    }

    fun captureCookie(cookie: String) {
        var _cookie by _cookieCaptured
        _cookie = cookie
    }

    fun step() {
        var __step by _step
        __step += 1
    }

    fun getServiceIntent(context: Context) = Intent(context, MaimaiVpnService::class.java)
    fun startService(context: Context, onSuccess: () -> Unit) {
        val intent = VpnService.prepare(context)
        val activity = context as ComponentActivity
        if (intent == null) {
            // already started??
            Toast.makeText(context, "服务已启动", LENGTH_SHORT).apply { show() }
            connectToService(activity, onSuccess)
        } else {
            (activity as MainActivity).launcher?.launch(intent)
        }
    }

    private var binder: MaimaiVpnService.MaimaiBinder? = null
    fun connectToService(activity: Activity, connected: () -> Unit) {
        if (binder != null) {
            connected()
            return
        }
        activity.startService(getServiceIntent(activity).setAction(MaimaiVpnService.ACTION_CONNECT))
        activity.bindService(getServiceIntent(activity), VpnSvcConn(
            {
                if (binder != null) {
                    throw IllegalStateException("what?") // todo debug
                }
                binder = it
                connected()
            }, { binder = null }
        ), Context.BIND_AUTO_CREATE)
    }

    internal class VpnSvcConn(
        private val onBinder: (MaimaiVpnService.MaimaiBinder) -> Unit,
        private val onDisconnect: () -> Unit
    ) : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            onBinder(service as MaimaiVpnService.MaimaiBinder)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            onDisconnect()
        }

    }
}