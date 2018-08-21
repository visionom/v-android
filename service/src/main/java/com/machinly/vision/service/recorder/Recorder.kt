package com.machinly.vision.service.recorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.util.*
import java.util.concurrent.SynchronousQueue
import kotlin.experimental.and
import kotlin.experimental.xor

class Recorder(private val voiceSQ: SynchronousQueue<ByteArray>) {

    private val bufferElements2Rec = 64 // want to play 2048 (2K) since 2 bytes we use only 1024
    private val bytesPerElement = 2 // 2 bytes in 16bit format
    private var recorder: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording = false

    companion object {
        private const val TAG = "recorder"
        private const val RECORDER_SAMPLE_RATE = 8000
        private const val RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO
        private const val RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT
    }

    fun startRecording() {
        Log.d(TAG, "startRecording() executed")
        recorder = AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLE_RATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, bufferElements2Rec * bytesPerElement)

        recorder?.startRecording()
        isRecording = true
        recordingThread = Thread(Runnable {
            while (isRecording) {
                // gets the voice output from microphone to byte format
                val sData = ShortArray(bufferElements2Rec)
                recorder?.read(sData, 0, bufferElements2Rec)
                val bData = short2byte(sData)
                Log.v(TAG, Arrays.toString(bData))
                voiceSQ.put(bData)
            }
        }, "AudioRecorder Thread")
        recordingThread?.start()
    }

    fun stopRecording() {
        Log.d(TAG, "stopRecording() executed")
        if (null != recorder) {
            isRecording = false
            recorder?.stop()
            recorder?.release()
            voiceSQ.clear()
            recorder = null
            recordingThread = null
        }
    }

    //convert short to byte
    private fun short2byte(sData: ShortArray): ByteArray {
        val shortArrSize = sData.size
        val bytes = ByteArray(shortArrSize * 2)
        for (i in 0 until shortArrSize) {
            bytes[i * 2] = (sData[i] and 0xff).toByte()
            bytes[i * 2 + 1] = (sData[i].toInt() shr (8) and 0xff).toByte()
            sData[i] = 0
        }
        return bytes
    }


}