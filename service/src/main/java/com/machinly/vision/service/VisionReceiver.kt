package com.machinly.vision.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class VisionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "vision_receiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "action:${intent.action}")
        when (intent.action) {
            "android.intent.action.USER_PRESENT", "com.machinly.vision.WAKEUP_RECORDER" ->
                context.startService(Intent(context, VisionService::class.java))
        }
    }
}
