package com.machinly.vision.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class VisionService : Service() {
    companion object {
        private const val TAG = "vision_service"
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.i(TAG, "onBind() executed")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate() executed")
    }
}


