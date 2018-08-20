package com.machinly.vision.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.IBinder
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.machinly.vision.service.aidl.IVisionAidlInterface

/**
 * TODO: document your custom view class.
 */
class VoiceDisplayView : View {

    private var mService: IVisionAidlInterface? = null
    private var bs: ByteArray? = null

    companion object {
        const val TAG = "vision_voice_view"
    }

    var exampleDrawable: Drawable? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        val intent = Intent("com.machinly.vision.service.VisionService")
        intent.setClassName("com.machinly.vision.service", "com.machinly.vision.service.VisionService")
        context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        Thread {
            while (true) {
                if (mService != null) {
                    val tbs = mService?.voiceBytes!!
                    if (tbs.isNotEmpty()) {
                        bs = tbs
                    }
                }
                postInvalidate()
                Thread.sleep(40)
            }
        }.start()
    }

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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom
        val middleWidth = contentWidth / 2 + paddingLeft

        // Draw the text.
        val paint = Paint()

        paint.color = Color.RED
        canvas.drawLine((middleWidth).toFloat(), 0F, (middleWidth).toFloat(), contentHeight.toFloat(), paint)
        paint.color = Color.GREEN
        if (bs != null) {
            // TODO: Audio processing is more complicated than I thought, I will do some research
//            var i = 0
//            while (i < bs!!.size) {
//                val l = ((bs!![i] + 128) * 256 + bs!![i + 1] + 128) / 100F
//                canvas.drawLine(i / 2F, 0F, i / 2F, l + 0F, paint)
//                i += 2
//            }
        }

        // Draw the example drawable on top of the text.
        exampleDrawable?.let {
            it.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight)
            it.draw(canvas)
        }
    }
}
