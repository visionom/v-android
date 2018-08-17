package com.machinly.vision.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class VisionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "vision_receiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "action:${intent.action}")
        Toast.makeText(context, "action:${intent.action}", Toast.LENGTH_SHORT)
        when (intent.action) {
            "android.intent.action.USER_PRESENT", "com.machinly.vision.service.WAKEUP_VISION" -> {
                context.startService(Intent(context, VisionService::class.java))
                Log.i(TAG, "action:${intent.action} is received")
            }
            else -> {
                Log.i(TAG, "action:${intent.action} is not allowed")
            }
        }
    }
}
