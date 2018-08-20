package com.machinly.vision.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.machinly.vision.app.R.*
import com.machinly.vision.common.VisionOption
import com.machinly.vision.common.VisionStatus
import com.machinly.vision.service.aidl.IVisionAidlInterface
import kotlinx.android.synthetic.main.activity_vision_main.*

class VisionMainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "vision_app"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_vision_main)
        wakeup()
        initView()
    }


    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
    }

    private fun wakeup() {
        intent = Intent("com.machinly.vision.service.WAKEUP_VISION")
        sendBroadcast(intent)
        intent = Intent("com.machinly.vision.service.VisionService")
        intent.setClassName("com.machinly.vision.service", "com.machinly.vision.service.VisionService")
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun initView() {
        checkStatus()
        recorderCtlBtn.setOnClickListener { recorderTrigger() }
    }

    private fun checkStatus() {
        Log.d(TAG, "check status")
        recorderCtlBtn.post {
            if (mService != null) {
                Log.d(TAG, mService?.getStatus(VisionOption.REC.ordinal).toString())
                when (mService?.getStatus(VisionOption.REC.ordinal)) {
                    VisionStatus.REC_ON.ordinal -> recorderCtlBtn.setText(R.string.recorder_stop)
                    VisionStatus.REC_OFF.ordinal -> recorderCtlBtn.setText(R.string.recorder_start)
                    else -> recorderCtlBtn.setText(R.string.recorder_start)
                }
            }
        }
    }

    private fun recorderTrigger() {
        if (mService != null) {
            mService?.trigger(VisionOption.REC.ordinal)
            checkStatus()
        }
    }

    private var mService: IVisionAidlInterface? = null

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected() executed")
            mService = IVisionAidlInterface.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "onServiceDisconnected() executed")
            mService = null
        }
    }
}
