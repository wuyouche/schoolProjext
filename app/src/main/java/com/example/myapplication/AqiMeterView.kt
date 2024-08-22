package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.min

class AqiMeterView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }
    private var aqi = 90


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val size = min(width, height)
        val strokeWidth = size / 10
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE

        val arcRect = RectF(
            strokeWidth / 2,
            strokeWidth / 2,
            width - strokeWidth / 2,
            height - strokeWidth / 2
        )


        paint.color = Color.GRAY
        canvas.drawArc(arcRect, 160f, 220f, false, paint)


        paint.color = getAqiColor(aqi)
        val sweepAngle = 220f * (aqi / 175f)
        canvas.drawArc(arcRect, 160f, sweepAngle, false, paint)


    }

    fun setAqi(newAqi: Int) {
        aqi = newAqi
        invalidate()
    }

    private fun getAqiColor(aqi: Int): Int {
        val color = when (aqi) {
            in 0..25 -> Color.BLUE
            in 25..50 -> Color.GREEN
            in 50..75 -> Color.parseColor("#FFA500") // Orange
            in 75..100 -> Color.RED
            in 100..125 -> Color.RED
            in 125..150 -> Color.RED
            in 150..175 -> Color.RED
            else -> Color.MAGENTA
        }
        Log.d("getAqiColor", "AQI: $aqi, Color: $color")
        return color
    }

}
