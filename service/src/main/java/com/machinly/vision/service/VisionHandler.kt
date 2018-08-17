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

    companion object {
        const val TAG = "vision_handler"
    }

    fun init() {
        initStatus()
        initPipeline()
    }

    fun initStatus() {
        setStatus(VisionStatus.REC_OFF)
        setStatus(VisionStatus.RECOG_OFF)
    }

    fun initPipeline() {
        Thread {
            while (true) {
                Log.d("test", Arrays.toString(voiceSQ.take()))
            }
        }
    }

    private fun checkStatus(status: VisionStatus): Boolean {
        if (statusMap.containsKey(status.option))
            return statusMap[status.option]!! == status
        return false
    }

    private fun setStatus(status: VisionStatus) {
        Log.i(TAG, statusMap.keys.toString())
        statusMap.plus(Pair(status.option, status))
    }

    fun getStatus(op: Int): VisionStatus {
//        if (VisionOption.values().size > op) {
//            val key = VisionOption.values()[op]
//            if (statusMap.containsKey(key))
//                return statusMap[key]!!
//        }
        return statusMap[VisionOption.REC]!!
    }


    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        when (msg.what) {
            VisionOption.REC.ordinal -> {
                if (checkStatus(VisionStatus.REC_OFF)) {
                    recorder.startRecording()
                    setStatus(VisionStatus.RECOG_ON)
                } else {
                    recorder.stopRecording()
                    setStatus(VisionStatus.RECOG_OFF)
                }
            }
            else -> {
                Log.e(TAG, "option ${VisionOption.valueOf(msg.what.toString())} not define")
            }
        }
    }
}