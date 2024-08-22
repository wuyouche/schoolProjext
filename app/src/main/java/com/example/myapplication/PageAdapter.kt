package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentadapterBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherFragment(
    private val currentAQI: MutableList<Int>,
    private val dayCould: MutableList<String>,
    private val highTemp: MutableList<String>,
    private val lowTemp: MutableList<String>,
    private val tenday: MutableList<String>,
    private val tendayCould: MutableList<String>,
    private val maxTemp: MutableList<String>,
    private val minTemp: MutableList<String>,
    private val nowCity: MutableList<String>,
    private val nowTemp123: MutableList<String>,
    private val image24: MutableList<String>,
    private val time24: MutableList<String>,
    private val temp24: MutableList<String>,
    private val page: Int,
    private val conut: Int
) : Fragment() {
    private lateinit var binding: FragmentadapterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentadapterBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nowTemp.text = changeTemp(nowTemp123[page])
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val linearLayoutManager2 =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val nowtemp: MutableList<String> = mutableListOf()
        val nowtemp2: MutableList<String> = mutableListOf()
        val nowtemp3: MutableList<String> = mutableListOf()
        val nowtemp4: MutableList<String> = mutableListOf()
        val nowtemp5: MutableList<String> = mutableListOf()
        val nowtemp6: MutableList<String> = mutableListOf()
        val nowtemp7: MutableList<String> = mutableListOf()
        var start = 0
        var end = 0
        var start2 = 0
        var end2 = 0
        when (page) {
            0 -> {
                start = 0
                end = conut
                start2 = 0
                end2 = 10
            }

            1 -> {
                start = conut
                end = conut * 2
                start2 = 10
                end2 = 20
            }

            2 -> {
                start = conut * 2
                end = conut * 3
                start2 = 20
                end2 = 30
            }

            3 -> {
                start = conut * 3
                end = conut * 4
                start2 = 30
                end2 = 40
            }

            4 -> {
                start = conut * 4
                end = conut * 5
                start2 = 40
                end2 = 50
            }

        }
        for (i in start until end) {
            nowtemp.add(temp24[i])
            nowtemp2.add(image24[i])
            nowtemp3.add(time24[i])
        }
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        var count_tenday = 0
        for (i in start2 until end2) {
            if (formattedDate == tenday[i]) {
                nowtemp4.add("今天")
                nowtemp5.add(tendayCould[i])
                nowtemp6.add(maxTemp[i])
                nowtemp7.add(minTemp[i])
                count_tenday += 1
            } else if (tenday[i] > formattedDate) {
                nowtemp4.add(tenday[i])
                nowtemp5.add(tendayCould[i])
                nowtemp6.add(maxTemp[i])
                nowtemp7.add(minTemp[i])
                count_tenday += 1
            }
        }
        if (count_tenday == 0) {
            for (i in start2 until end2) {
                nowtemp4.add(tenday[i])
                nowtemp5.add(tendayCould[i])
                nowtemp6.add(maxTemp[i])
                nowtemp7.add(minTemp[i])
                count_tenday += 1
            }

        }
        binding.nowCity.text = nowCity[page]
        binding.highTemp.text = "H:${changeTemp(highTemp[page])}"
        binding.lowTemp.text = "L:${changeTemp(lowTemp[page])}"
        binding.nowCloud.text = dayCould[page]
        binding.temp24data.layoutManager = linearLayoutManager
        val weatherAdapter =
            HourWeather(requireContext(), nowtemp2, nowtemp3, nowtemp)
        binding.temp24data.adapter = weatherAdapter

        binding.tenDayData.layoutManager = linearLayoutManager2
        val tendayWeather = TenDayWeather(
            requireContext(),
            nowtemp4,
            nowtemp5,
            nowtemp6,
            nowtemp7,
            nowTemp123[page]
        )
        binding.tenDayData.adapter = tendayWeather
        Log.d("currentAQI", currentAQI[page].toString())
        binding.aqiMeterView.setAqi(currentAQI[page])
        if (currentAQI[page] > 0 && currentAQI[page] < 50) {
            binding.leavl.text = "優良"
        } else if (currentAQI[page] in 51..99) {
            binding.leavl.text = "普通"
        } else {
            binding.leavl.text = "不健康"
        }

        binding.backgroundColor.setCardBackgroundColor(getAqiColor(currentAQI[page]))
        binding.AQIText.text = currentAQI[page].toString()

        when (dayCould[page]) {
            "sunny" -> binding.cloudBack.setBackgroundResource(R.drawable.sunny2)
            "rain" -> binding.cloudBack.setBackgroundResource(R.drawable.rain2)
            "thunder" -> binding.cloudBack.setBackgroundResource(R.drawable.thunder2)
            "overcast" -> binding.cloudBack.setBackgroundResource(R.drawable.overcast2)
            "cloudy" -> binding.cloudBack.setBackgroundResource(R.drawable.cloudy2)
        }


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

    private fun changeTemp(temp: String): String {
        val sh = requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)
        var final: String = "0"
        if (sh.getString("temp", "F") != "H") {
            final = ((temp.substring(0, 2).toDouble() * 1.8) + 32).toString().substring(0, 2)
        } else {
            final = temp.substring(0, 2).toInt().toString()
        }
        return final
    }

}

