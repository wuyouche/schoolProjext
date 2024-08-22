package com.example.myapplication

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TenDayWeather(
    private val context: Context,
    private val tenday: MutableList<String>,
    private val tendayCould: MutableList<String>,
    private val maxTemp: MutableList<String>,
    private val minTemp: MutableList<String>,
    private val dayTemp: String
) : RecyclerView.Adapter<TenDayWeather.MyViewHolder>() {
    inner class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val day: TextView = itemview.findViewById(R.id.day)
        val dayCould: ImageView = itemview.findViewById(R.id.dayCloud)
        val minTemp: TextView = itemview.findViewById(R.id.minTemp)
        val maxTemp: TextView = itemview.findViewById(R.id.maxTemp)
        val seekBarCold: SeekBar = itemview.findViewById(R.id.seekBarCold)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.ten_dat_weather, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tenday.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (tenday[position] == "今天") {
            holder.day.text = tenday[position]
        } else {
            holder.day.text = tenday[position].substring(6, 10)
        }

        when (tendayCould[position]) {
            "cloudy" -> holder.dayCould.setBackgroundResource(R.drawable.cloudy)
            "sunny" -> holder.dayCould.setBackgroundResource(R.drawable.sunny)
            "overcast" -> holder.dayCould.setBackgroundResource(R.drawable.overcast)
            "rain" -> holder.dayCould.setBackgroundResource(R.drawable.rain)
            "thunder" -> holder.dayCould.setBackgroundResource(R.drawable.thunder)

        }
        var isCold = true
        isCold = (maxTemp[position].substring(0, 2).toInt() + minTemp[position].substring(0, 2)
            .toInt()) / 2.toInt() >= dayTemp.substring(0, 2).toInt()
        holder.maxTemp.text = changeTemp(maxTemp[position])
        holder.minTemp.text = changeTemp(minTemp[position])
        holder.seekBarCold.isEnabled = false
        setupSeekBar(
            holder.seekBarCold,
            minTemp[position].substring(0, 2).toInt(),
            maxTemp[position].substring(0, 2).toInt(),
            isCold
        )
    }

    private fun setupSeekBar(seekBar: SeekBar, minTemp: Int, maxTemp: Int, isCold: Boolean) {
        seekBar.max = maxTemp - minTemp
        seekBar.progress = dayTemp.substring(0, 2).toInt() - minTemp
        updateSeekBarColor(seekBar, minTemp, maxTemp, isCold)
    }

    private fun updateSeekBarColor(seekBar: SeekBar?, minTemp: Int, maxTemp: Int, isCold: Boolean) {
        seekBar?.let {
            val startColor = if (isCold) ContextCompat.getColor(
                context,
                R.color.cold_start
            ) else ContextCompat.getColor(context, R.color.warm_start)
            val endColor = if (isCold) ContextCompat.getColor(
                context,
                R.color.cold_end
            ) else ContextCompat.getColor(context, R.color.warm_end)

            val ratio = seekBar.progress.toFloat() / seekBar.max
            val color = blendColors(startColor, endColor, ratio)

            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(startColor, color, endColor)
            ).apply {
                cornerRadius = 10f
            }
            seekBar.progressDrawable = gradientDrawable
        }
    }

    private fun blendColors(startColor: Int, endColor: Int, ratio: Float): Int {
        val inverseRatio = 1 - ratio
        val r = (Color.red(startColor) * inverseRatio + Color.red(endColor) * ratio).toInt()
        val g = (Color.green(startColor) * inverseRatio + Color.green(endColor) * ratio).toInt()
        val b = (Color.blue(startColor) * inverseRatio + Color.blue(endColor) * ratio).toInt()
        return Color.rgb(r, g, b)
    }

    private fun changeTemp(temp: String): String {
        val sh = context.getSharedPreferences("state", Context.MODE_PRIVATE)
        var final: String = "0"
        if (sh.getString("temp", "F") != "H") {
            final = ((temp.substring(0, 2).toDouble() * 1.8) + 32).toString().substring(0, 2)
        } else {
            final = temp.substring(0, 2).toInt().toString()
        }
        return final
    }
}
