package com.machinly.vision.service

import android.os.Looper
import android.os.Message
import android.util.Log
import java.util.*
import java.util.concurrent.SynchronousQueue
import kotlin.collections.HashMap
import com.machinly.vision.service.recorder.Recorder
import com.machinly.vision.common.*

class VisionHandler internal constructor(looper: Looper) : android.os.Handler(looper) {
    private var config = VisionBuilder.GetDefaultConfig()
    private var voiceSQ = SynchronousQueue<ByteArray>()
    private var recorder = Recorder(voiceSQ)
    private var statusMap: Map<VisionOption, VisionStatus> = HashMap()
    private val vmgr = VisionStatusMgr()

    companion object {
        const val TAG = "vision_handler"
    }

    fun init() {
        initPipeline()
    }

    fun initPipeline() {
        Thread {
            while (true) {
                Log.d("test", Arrays.toString(voiceSQ.take()))
            }
        }
    }

    fun GetStatus(optionOrder: Int): VisionStatus {
        vmgr.PrintStatusMap()
        return vmgr.GetStatus(optionOrder)
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        Log.d(TAG, msg.what.toString())
        when (msg.what) {
            VisionOption.REC.ordinal -> {
                if (vmgr.CheckStatus(VisionStatus.REC_OFF)) {
                    recorder.startRecording()
                    vmgr.SetStatus(VisionStatus.REC_ON)
                } else {
                    recorder.stopRecording()
                    vmgr.SetStatus(VisionStatus.REC_OFF)
                }
            }
            else -> {
                Log.e(TAG, "option ${VisionOption.valueOf(msg.what.toString())} not define")
            }
        }
    }
}