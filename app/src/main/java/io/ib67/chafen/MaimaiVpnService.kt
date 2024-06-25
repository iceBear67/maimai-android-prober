package io.ib67.chafen

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat


class MaimaiVpnService : VpnService() {
    companion object {
        const val ACTION_CONNECT = "io.ib67.chafen.proxy.START"
        const val ACTION_DISCONNECT = "io.ib67.chafen.proxy.STOP"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "maimai-prober"
    }

    class MaimaiBinder(val svc: MaimaiVpnService) : Binder() {}

    override fun onBind(intent: Intent?): IBinder {
        return MaimaiBinder(this)
    }

    override fun onDestroy() {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        val builder = Builder()
        builder.addAllowedApplication("com.tencent.mm")
        return START_NOT_STICKY
    }


    private fun createNotification(): Notification {
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        val channel = NotificationChannel(
            CHANNEL_ID,
            "LxnsProber",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val copyPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, MaiNotificationReceiver::class.java).apply {
                setAction("prober.intent.ACTION_COPY_TO_CLIPBOARD")
                putExtra("url", "........") //todo
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val openWeChatIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, MaiNotificationReceiver::class.java).apply {
                setAction("prober.intent.ACTION_START_WECHAT")
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("查分器")
            .setContentText("请根据 APP 内提示完成操作")
            .setSmallIcon(R.drawable.sym_def_app_icon)
            .addAction(R.drawable.ic_input_get, "复制地址", copyPendingIntent)
            .addAction(R.drawable.ic_media_play, "打开微信", openWeChatIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        return builder.build()
    }
}