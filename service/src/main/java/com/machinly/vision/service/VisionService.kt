package com.machinly.vision.service

import android.app.Service
import android.content.Intent
import android.os.HandlerThread
import android.os.IBinder
import android.os.Message
import android.os.Process
import android.util.Log
import com.machinly.vision.service.aidl.IVisionAidlInterface
import com.machinly.vision.common.*

class VisionService : Service() {
    companion object {
        private const val TAG = "vision_service"
    }

    private var mHandler: VisionHandler? = null

    override fun onBind(intent: Intent): IBinder? {
        Log.i(TAG, "onBind() executed")
        return iVisionAidl
    }

    override fun onCreate() {
        super.onCreate()
        initHandler()
        Log.i(TAG, "onCreate() executed")
    }

    private fun initHandler() {
        val thread = HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()

        val mServiceLooper = thread.looper
        mHandler = VisionHandler(mServiceLooper)
        mHandler?.init()
        visionTrigger(VisionOption.REC.ordinal)
    }

    private fun visionTrigger(option: Int) {
        Log.d(TAG, "visionTrigger executed")
        mHandler?.sendEmptyMessage(option)
    }

    private var iVisionAidl = object : IVisionAidlInterface.Stub() {

        override fun trigger(option: Int) {
            visionTrigger(option)
        }

        override fun getStatus(option: Int): Int {
            return mHandler?.getStatus(option)!!.ordinal
        }

        override fun getVoiceBytes(): ByteArray {
            return mHandler?.getAllAudioCache()!!
        }

        override fun getStringAfterID(id: Int): Array<String> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}


