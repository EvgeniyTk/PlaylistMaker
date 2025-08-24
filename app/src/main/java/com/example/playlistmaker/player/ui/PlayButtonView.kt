package com.example.playlistmaker.player.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.use
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R
import androidx.core.graphics.scale

class PlayButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var playBitmapOriginal: Bitmap? = null
    private var pauseBitmapOriginal: Bitmap? = null

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null

    private var isPlaying = false

    private var listener: (() -> Unit)? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.PlayButtonView).use { ta ->
            val playResId = ta.getResourceId(R.styleable.PlayButtonView_playIcon, 0)
            val pauseResId = ta.getResourceId(R.styleable.PlayButtonView_pauseIcon, 0)


            if (playResId != 0) {
                playBitmapOriginal = AppCompatResources.getDrawable(context, playResId)?.toBitmap()
            }
            if (pauseResId != 0) {
                pauseBitmapOriginal = AppCompatResources.getDrawable(context, pauseResId)?.toBitmap()
            }

            playBitmap = playBitmapOriginal
            pauseBitmap = pauseBitmapOriginal
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val playW = playBitmapOriginal?.width ?: 84
        val playH = playBitmapOriginal?.height ?: 84
        val pauseW = pauseBitmapOriginal?.width ?: 84
        val pauseH = pauseBitmapOriginal?.height ?: 84

        val desiredWidth = maxOf(playW, pauseW)
        val desiredHeight = maxOf(playH, pauseH)

        val measuredWidth = resolveSize(desiredWidth, widthMeasureSpec)
        val measuredHeight = resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (w > 0 && h > 0) {
            playBitmap = playBitmapOriginal?.scale(w, h)
            pauseBitmap = pauseBitmapOriginal?.scale(w, h)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bitmap = if (isPlaying) pauseBitmap else playBitmap
        bitmap?.let {
            val left = (width - it.width) / 2f
            val top = (height - it.height) / 2f
            canvas.drawBitmap(it, left, top, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        listener?.invoke()
        return true
    }

    fun setPlaying(playing: Boolean) {
        isPlaying = playing
        invalidate()
    }

    fun setOnPlaybackClickListener(l: () -> Unit) {
        listener = l
    }
}
