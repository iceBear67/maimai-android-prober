package io.ib67.chafen

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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

    fun info(message: String) {
        logs.add("$logTime $message")
    }

    fun step() {
    }
}