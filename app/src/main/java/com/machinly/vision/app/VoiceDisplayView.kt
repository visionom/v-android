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
    private var volumeMax = 10F

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

        drawCurVoice(
                canvas,
                contentWidth.toFloat(),
                contentHeight.toFloat(),
                paddingLeft.toFloat(),
                paddingTop.toFloat(),
                paddingRight.toFloat(),
                paddingBottom.toFloat()
        )


        exampleDrawable?.let {
            it.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight)
            it.draw(canvas)
        }
    }

    private fun drawLine(canvas: Canvas, cw: Float, ch: Float, pl: Float, pt: Float, pr: Float, pb: Float) {

        val l = pl
        val t = pt
        val r = cw + pl
        val b = ch + pt

        val mw = cw / 2 + pl
        val mh = ch / 2 + pt

        val paint = Paint()

        paint.color = Color.RED
        paint.alpha = 200
        canvas.drawLine(mw, b, mw, t, paint)

        paint.color = Color.BLACK
        canvas.drawLine(l, mh, r, mh, paint)

    }

    private fun drawCurVoice(canvas: Canvas, cw: Float, ch: Float, pl: Float, pt: Float, pr: Float, pb: Float) {
        val l = pl
        val t = pt
        val r = cw + pl
        val b = ch + pt

        val mw = cw / 2 + pl
        val mh = ch / 2 + pt

        // Draw the text.
        val paint = Paint()

        paint.color = Color.GREEN
        paint.strokeWidth = 2F
        if (bs != null) {
            var i = 0
            val stepW = cw / bs!!.size.toFloat()
            while (i < bs!!.size - 2) {
                val av = (bs!![i]) + ((bs!![i + 1] + 0) shl 8)
                val bv = (bs!![i + 2]) + ((bs!![i + 3] + 0) shl 8)

                if (Math.abs(av) > volumeMax) {
                    volumeMax = Math.abs(av).toFloat()
                }

                if (Math.abs(bv) > volumeMax) {
                    volumeMax = Math.abs(bv).toFloat()
                }

                val ay = (av / volumeMax) * ch + mh
                val by = (bv / volumeMax) * ch + mh
                val ax = i * stepW + pl
                val bx = (i + 1) * stepW + pl

                canvas.drawLine(ax, ay, bx, by, paint)
                i += 2
            }
        }

    }
}
