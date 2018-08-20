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
    private val vmgr = VisionStatusMgr()
    private var audioCache: Queue<ByteArray> = LinkedList<ByteArray>()

    companion object {
        const val TAG = "vision_handler"
        const val CacheSize = 2
    }

    fun init() {
        initPipeline()
    }

    fun initPipeline() {
        Thread {
            while (true) {
                val audioBytes = voiceSQ.take()
                Log.v(TAG, Arrays.toString(voiceSQ.take()))
                Log.d(TAG, "audio size: ${audioBytes.size}")
                audioCache.offer(audioBytes)
                Log.d(TAG, "cache size: ${audioCache.size}")
                if (audioCache.size > CacheSize) {
                    audioCache.remove()
                }
            }
        }.start()
    }

    fun getStatus(optionOrder: Int): VisionStatus {
        return vmgr.GetStatus(optionOrder)
    }

    fun getAllAudioCache(): ByteArray {
//        val os = ByteArrayOutputStream()
//        audioCache.iterator().let {
//            while (it.hasNext()) {
//                os.write(it.next())
//            }
//        }
//        return os.toByteArray()
        if (audioCache.size > 0)
            return audioCache.element()
        return ByteArray(0)
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
                    audioCache.clear()
                    vmgr.SetStatus(VisionStatus.REC_OFF)
                }
                vmgr.PrintStatusMap()
            }
            else -> {
                Log.e(TAG, "option ${VisionOption.valueOf(msg.what.toString())} not define")
            }
        }
    }
}