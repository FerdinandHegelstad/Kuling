package com.example.team22.utilities

import android.app.Service
import android.content.Intent
import android.os.IBinder

/*Class which sole purpose is to register when app is force-closed by user.
* This is to set boolean value to false from shared preferences key "from_settings", so that when user re-enters app, they do not automatically get sent to settings as first view.*/
class OnClearFromRecentService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val filename = "com.example.team22.user_preferences"
        val preferences = application.getSharedPreferences(filename, 0)
        with(preferences.edit()){
            putBoolean("from_settings", false)
            commit()
        }
        stopSelf()
    }
}