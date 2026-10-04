package com.meshchat

import android.app.Application
import com.meshchat.repo.MeshRepository
import com.meshchat.service.Notifier

class MeshChatApp : Application() {
    lateinit var repo: MeshRepository
        private set

    override fun onCreate() {
        super.onCreate()
        Notifier.createChannels(this)
        repo = MeshRepository(this)
    }
}
