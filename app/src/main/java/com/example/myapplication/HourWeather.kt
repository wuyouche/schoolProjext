package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HourWeather(
    private val context: Context,
    private val weatherList: MutableList<String>,
    private val weatherList2: MutableList<String>,
    private val weatherList3: MutableList<String>
) :
    RecyclerView.Adapter<HourWeather.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: ImageView = itemView.findViewById(R.id.weahter)
        val text2: TextView = itemView.findViewById(R.id.hour)
        val text3: TextView = itemView.findViewById(R.id.weatherIcon)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hourweather, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.text2.text = changeTemp(weatherList2[position])
        when (weatherList[position]) {
            "cloudy" -> holder.text.setBackgroundResource(R.drawable.cloudy)
            "sunny" -> holder.text.setBackgroundResource(R.drawable.sunny)
            "overcast" -> holder.text.setBackgroundResource(R.drawable.overcast)
            "rain" -> holder.text.setBackgroundResource(R.drawable.rain)
            "thunder" -> holder.text.setBackgroundResource(R.drawable.thunder)

        }
        holder.text3.text = weatherList3[position].toString().subSequence(0, 2)
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
