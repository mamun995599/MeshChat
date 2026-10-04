package com.meshchat.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.meshchat.MeshChatApp

/**
 * Foreground service (type connectedDevice) that keeps the BLE mesh alive while the app is in the background.
 * Android 8+ kills plain background services; without this the node stops relaying within minutes.
 * It must be started from a visible Activity (Android 12+ forbids starting it from the background).
 */
class MeshService : Service() {

    override fun onCreate() {
        super.onCreate()
        Notifier.createChannels(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val repo = (application as MeshChatApp).repo
        if (intent?.action == ACTION_STOP) {
            repo.stopMesh()
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE else 0
        ServiceCompat.startForeground(this, Notifier.ID_SERVICE, Notifier.serviceNotification(this), type)
        repo.startMesh()
        return START_STICKY
    }

    override fun onDestroy() {
        (application as MeshChatApp).repo.stopMesh()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_STOP = "com.meshchat.action.STOP"
    }
}
