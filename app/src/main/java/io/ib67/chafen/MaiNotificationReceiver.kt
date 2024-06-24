package io.ib67.chafen

import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast


class MaiNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            "prober.intent.ACTION_COPY_TO_CLIPBOARD" -> {
                val clipboard: ClipboardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("server address", intent.getStringExtra("url"))
                clipboard.setPrimaryClip(clip)

                Toast.makeText(context, "地址已复制到粘贴板", Toast.LENGTH_SHORT).show()
            }

            "prober.intent.ACTION_START_WECHAT" -> {
                Intent().apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                    component = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
                    context.startActivity(this)
                }
            }
        }
    }
}