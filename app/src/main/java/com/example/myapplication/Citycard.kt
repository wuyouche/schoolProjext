package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.CityListBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale

@Suppress("DEPRECATION")
class Citycard(
    val context: Context,
    private val originalCityName: MutableList<String>,
    private val originalLowTemp: MutableList<String>,
    private val originalHighTemp: MutableList<String>,
    private val originalNowTemp: MutableList<String>,
    private val originalNowCould: MutableList<String>,
    private val clearSearchBox: () -> Unit,
    private var deOrAd: Int
) : RecyclerView.Adapter<Citycard.MyViewholder>() {
    private var cityName = originalCityName.toMutableList()
    private var lowTemp = originalLowTemp.toMutableList()
    private var highTemp = originalHighTemp.toMutableList()
    private var nowTemp = originalNowTemp.toMutableList()
    private var nowCould = originalNowCould.toMutableList()
    private var filteredCityName = cityName.toMutableList()

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        @SuppressLint("NotifyDataSetChanged")
        override fun run() {
            notifyDataSetChanged()
            handler.postDelayed(this, 60000)
        }
    }

    inner class MyViewholder(val binding: CityListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.cityListNowCity.text = changeCity(cityName[adapterPosition])
            binding.cityListLowTemp.text = changeTemp(lowTemp[adapterPosition])
            binding.cityListHighTemp.text = changeTemp(highTemp[adapterPosition])
            binding.cityListNowTemp.text = changeTemp(nowTemp[adapterPosition])
            binding.cityListDayCould.text = changeCloud(nowCould[adapterPosition])
            when (nowCould[adapterPosition]) {
                "sunny" -> binding.cloudBackground.setBackgroundResource(R.drawable.sunny2)
                "rain" -> binding.cloudBackground.setBackgroundResource(R.drawable.rain2)
                "thunder" -> binding.cloudBackground.setBackgroundResource(R.drawable.thunder2)
                "overcast" -> binding.cloudBackground.setBackgroundResource(R.drawable.overcast2)
                "cloudy" -> binding.cloudBackground.setBackgroundResource(R.drawable.cloudy2)
            }
            if (deOrAd == 1) {
                binding.deOrAd.visibility = View.VISIBLE
                binding.deOrAd.setOnClickListener {
                    deleteItem(adapterPosition)
                }
                binding.deOrAd.setBackgroundResource(R.drawable.baseline_home_mini_24)
            } else if (deOrAd == 2) {
                binding.deOrAd.visibility = View.VISIBLE
                binding.deOrAd.setOnClickListener {
                    addCity()
                }
                binding.deOrAd.setBackgroundResource(R.drawable.baseline_add_24)
            } else {
                binding.deOrAd.visibility = View.GONE
            }
            updateTime(binding)
        }

        private fun changeCity(city: String): String {
            var final = ""
            val sh = context.getSharedPreferences("state", Context.MODE_PRIVATE)
            if (sh.getString("lan", "zh") == "en") {
                when (city) {
                    "新北市" -> final = "newTaipeiCity"
                    "台北市" -> final = "taipei"
                    "桃園市" -> final = "taoyuan"
                    "台中市" -> final = "taichung"
                    "台南市" -> final = "tainan"
                }
            } else {
                final = city
            }
            return final
        }

        private fun changeCloud(cloud: String): String {
            var final = ""
            val sh = context.getSharedPreferences("state", Context.MODE_PRIVATE)
            if (sh.getString("lan", "zh") == "zh") {
                when (cloud) {
                    "cloudy" -> final = "多雲"
                    "overcast" -> final = "有霧"
                    "rain" -> final = "下雨"
                    "sunny" -> final = "晴天"
                    "thunder" -> final = "雷"
                }
            } else {
                final = cloud
            }
            return final
        }


        private fun updateTime(binding: CityListBinding) {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("H:mm", Locale.getDefault())
            binding.cityListTime.text = sdf.format(calendar.time)
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun addCity() {
            val newCity = cityName[position]
            val newLow = lowTemp[position]
            val newHigh = highTemp[position]
            val newCould = nowCould[position]
            val newTemp = nowTemp[position]


            if (originalCityName.indexOf(cityName[position]) == -1) {
                listOf(cityName, lowTemp, highTemp, nowTemp, nowCould).forEachIndexed { i, list ->
                    list.clear()
                    list.addAll(
                        listOf(
                            originalCityName,
                            originalLowTemp,
                            originalHighTemp,
                            originalNowTemp,
                            originalNowCould
                        )[i]
                    )
                    list.add(
                        listOf(
                            newCity,
                            newLow,
                            newHigh,
                            newTemp,
                            newCould
                        )[i]
                    )
                }

                listOf(
                    originalCityName,
                    originalLowTemp,
                    originalHighTemp,
                    originalNowTemp,
                    originalNowCould
                ).forEachIndexed { i, list ->
                    list.add(
                        listOf(
                            newCity,
                            newLow,
                            newHigh,
                            newTemp,
                            newCould
                        )[i]
                    )
                }

                saveData()
                notifyDataSetChanged()
                clearSearchBox()

            }
            Log.d("index.toString", position.toString())
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        val binding = CityListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewholder(binding)
    }

    override fun getItemCount(): Int {
        return cityName.size
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        holder.bind(position)
    }

    fun swapItems(fromPosition: Int, toPosition: Int) {
        Collections.swap(cityName, fromPosition, toPosition)
        Collections.swap(lowTemp, fromPosition, toPosition)
        Collections.swap(highTemp, fromPosition, toPosition)
        Collections.swap(nowTemp, fromPosition, toPosition)
        Collections.swap(nowCould, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        saveData()
    }

    private fun saveData() {
        val sharedPreferences = context.getSharedPreferences("cityData", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("cityName", cityName.joinToString(","))
            putString("lowTemp", lowTemp.joinToString(","))
            putString("highTemp", highTemp.joinToString(","))
            putString("nowTemp", nowTemp.joinToString(","))
            putString("nowCould", nowCould.joinToString(","))
            apply()
        }
    }

    init {
        handler.post(updateTimeRunnable)
    }

    fun stopUpdating() {
        handler.removeCallbacks(updateTimeRunnable)
    }

    fun deleteItem(position: Int) {
        if (position < 0 || position >= cityName.size) {
            Log.e("Citycard", "Invalid position: $position")
            return
        }
        listOf(
            nowTemp,
            cityName,
            lowTemp,
            highTemp,
            nowCould
        ).forEachIndexed { i, list ->
            list.removeAt(
                position
            )
        }
        filteredCityName.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
        saveData()
    }


    fun filter(query: String) {
        deOrAd = 2
        Log.d("FilterFunction", "Received query: $query")

        val sharedPrefs = context.getSharedPreferences("cityDataAll", Context.MODE_PRIVATE)

        val cityName2 =
            sharedPrefs.getString("cityName", "")?.split(",")?.toMutableList() ?: mutableListOf()
        Log.d("jijiji", cityName2.toString())
        val lowTemp2 =
            sharedPrefs.getString("lowTemp", "")?.split(",")?.toMutableList() ?: mutableListOf()
        val highTemp2 =
            sharedPrefs.getString("highTemp", "")?.split(",")?.toMutableList() ?: mutableListOf()
        val nowTemp2 =
            sharedPrefs.getString("nowTemp", "")?.split(",")?.toMutableList() ?: mutableListOf()
        val nowCould2 =
            sharedPrefs.getString("nowCould", "")?.split(",")?.toMutableList() ?: mutableListOf()
        val sp = context.getSharedPreferences("state", Context.MODE_PRIVATE)
        val newCity: MutableList<String> = mutableListOf()
        if (sp.getString("lan", "zh") == "en") {

            cityName2.forEachIndexed { index, city ->
                when (city) {
                    "新北市" -> newCity.add("newTaipeiCity")
                    "台北市" -> newCity.add("taipei")
                    "桃園市" -> newCity.add("taoyuan")
                    "台中市" -> newCity.add("taichung")
                    "台南市" -> newCity.add("tainan")
                }

            }
        } else {
            cityName2.forEach { text ->
                newCity.add(text)
            }
        }
        cityName.clear()
        lowTemp.clear()
        highTemp.clear()
        nowTemp.clear()
        nowCould.clear()

        if (query.isEmpty()) {
            cityName.addAll(originalCityName)
            lowTemp.addAll(originalLowTemp)
            highTemp.addAll(originalHighTemp)
            nowTemp.addAll(originalNowTemp)
            nowCould.addAll(originalNowCould)
            deOrAd = 0
        } else {
            for (i in cityName2.indices) {
                if (newCity[i].contains(query, ignoreCase = true)) {
                    cityName.add(cityName2[i])
                    lowTemp.add(lowTemp2[i])
                    highTemp.add(highTemp2[i])
                    nowTemp.add(nowTemp2[i])
                    nowCould.add(nowCould2[i])
                }
            }
        }

        notifyDataSetChanged()
    }

    fun deOrAd(): Int {
        return deOrAd
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
