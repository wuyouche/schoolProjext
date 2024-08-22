package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.databinding.ActivityMainBinding
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val cityList: MutableList<String> = mutableListOf()
    private val nowTemp: MutableList<String> = mutableListOf()
    private val highTemp: MutableList<String> = mutableListOf()
    private val nowCity: MutableList<String> = mutableListOf()
    private val dayCould: MutableList<String> = mutableListOf()
    private val lowTemp: MutableList<String> = mutableListOf()
    private val hourWeather: MutableList<String> = mutableListOf()
    private var hourWeatherCount: Int = 0
    private val hourTimeList: MutableList<String> = mutableListOf()
    private val tenday: MutableList<String> = mutableListOf()
    private val tendayCould: MutableList<String> = mutableListOf()
    private val maxTemp: MutableList<String> = mutableListOf()
    private val minTemp: MutableList<String> = mutableListOf()
    private val hourWeatherImage: MutableList<String> = mutableListOf()
    private val currentAQI: MutableList<Int> = mutableListOf()
    private var have: Int = 0
    private var have2: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        loadLocale()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        parseXML()
        saveData()
        setupIndicators(cityList.size)
        updateIndicators(0)
        val adapter = MyPagerAdapter(supportFragmentManager)
        binding.vewpager.adapter = adapter
        binding.menu.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right,
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, BlankFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.vewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                updateIndicators(position)
            }
        })


    }

    @SuppressLint("SimpleDateFormat")
    private fun parseXML() {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        val sharedPreferences = getSharedPreferences("cityData", Context.MODE_PRIVATE)
        val haveCity = sharedPreferences.getString("cityName", "")?.split(",")?.toMutableList()
        if (haveCity != null) {
            for (i in haveCity.indices) {
                when (haveCity[i]) {
                    "台北市" -> cityList.add("taipei.xml")
                    "新北市" -> cityList.add("current.xml")
                    "台南市" -> cityList.add("tainan.xml")
                    "桃園市" -> cityList.add("taoyuan.xml")
                    "台中市" -> cityList.add("taichung.xml")
                }
            }
        }
        have = sharedPreferences.getInt("have", 0)
        if (have == 0) {
            cityList.add("taipei.xml")
            cityList.add("current.xml")
            cityList.add("tainan.xml")
            cityList.add("taoyuan.xml")
            cityList.add("taichung.xml")
            have = 1
        }
        val calendar = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm")
        val currentTime = timeFormat.format(calendar.time)

        for (currentCity in cityList) {
            val doc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(assets.open(currentCity))
            val nodeList = doc.getElementsByTagName("time")
            val nodeList2 = doc.getElementsByTagName("day")
            val hourWeatherImageTag = doc.getElementsByTagName("weather_condition")
            for (i in 0 until 10) {
                tenday.add(
                    (nodeList2.item(i) as Element).getElementsByTagName("date").item(0).textContent
                )
                tendayCould.add(
                    (nodeList2.item(i) as Element).getElementsByTagName("weather_condition")
                        .item(0).textContent
                )
                maxTemp.add(
                    (nodeList2.item(i) as Element).getElementsByTagName("high_temperature")
                        .item(0).textContent
                )
                minTemp.add(
                    (nodeList2.item(i) as Element).getElementsByTagName("low_temperature")
                        .item(0).textContent
                )

            }
            currentAQI.add(
                doc.getElementsByTagName("current_aqi").item(0).textContent.toInt()
            )
            var high = ""
            var low = ""
            for (i in 0 until 10) {
                if (formattedDate == (nodeList2.item(i) as Element).getElementsByTagName("date")
                        .item(0).textContent
                ) {
                    high = (nodeList2.item(i) as Element).getElementsByTagName("high_temperature")
                        .item(0).textContent
                    low = (nodeList2.item(i) as Element).getElementsByTagName("low_temperature")
                        .item(0).textContent
                }
            }
            highTemp.add(
                high
            )
            lowTemp.add(
                low
            )
            val hourTempTag = doc.getElementsByTagName("temperature")
            nowCity.add(doc.getElementsByTagName("city").item(0).textContent)
            for (i in 0 until nodeList.length) {
                val nodeTime = nodeList.item(i).textContent
                if (currentTime.substring(0, 2) == nodeTime.substring(0, 2)) {
                    nowTemp.add(hourTempTag.item(i).textContent)
                    hourTimeList.add("現在")
                    dayCould.add(hourWeatherImageTag.item(i).textContent)
                    hourWeatherCount += 1
                    hourWeather.add(hourTempTag.item(i).textContent)
                    hourWeatherImage.add(hourWeatherImageTag.item(i).textContent)
                    Log.d("count", nowTemp.toString())
                } else if (currentTime <= nodeTime) {
                    hourTimeList.add(nodeList.item(i).textContent)
                    hourWeather.add(hourTempTag.item(i).textContent)
                    hourWeatherImage.add(hourWeatherImageTag.item(i).textContent)
                    hourWeatherCount += 1
                }
            }

        }


    }

    private fun saveData() {
        val sp = this.getSharedPreferences("cityData", Context.MODE_PRIVATE)
        val sp2 = this.getSharedPreferences("cityDataAll", Context.MODE_PRIVATE)
        if (sp.getString("cityName", "") == "") {
            with(sp.edit()) {
                putString("cityName", nowCity.joinToString(","))
                putString("lowTemp", lowTemp.joinToString(","))
                putString("highTemp", highTemp.joinToString(","))
                putString("nowTemp", nowTemp.joinToString(","))
                putString("nowCould", dayCould.joinToString(","))
                putInt("have", 1)
                apply()
            }
        }
        val newTemp: MutableList<String> = mutableListOf()
        val newCloud: MutableList<String> = mutableListOf()
        var low: MutableList<String> = mutableListOf()
        var high: MutableList<String> = mutableListOf()
        with(sp.edit()) {
            remove("nowTemp")
            remove("nowCould")
            remove("highTemp")
            remove("lowTemp")
            nowCity.forEachIndexed { i, city ->
                when (city) {
                    "台北市" -> {
                        newTemp.add(nowTemp[i])
                        newCloud.add(dayCould[i])
                        low.add(lowTemp[i])
                        high.add(highTemp[i])
                    }

                    "新北市" -> {
                        newTemp.add(nowTemp[i])
                        newCloud.add(dayCould[i])
                        low.add(lowTemp[i])
                        high.add(highTemp[i])
                    }

                    "台南市" -> {
                        newTemp.add(nowTemp[i])
                        newCloud.add(dayCould[i])
                        low.add(lowTemp[i])
                        high.add(highTemp[i])
                    }

                    "桃園市" -> {
                        newTemp.add(nowTemp[i])
                        newCloud.add(dayCould[i])
                        low.add(lowTemp[i])
                        high.add(highTemp[i])
                    }

                    "台中市" -> {
                        newTemp.add(nowTemp[i])
                        newCloud.add(dayCould[i])
                        low.add(lowTemp[i])
                        high.add(highTemp[i])
                    }
                }
            }
            putString("nowCould", newCloud.joinToString(","))
            putString("nowTemp", newTemp.joinToString(","))
            putString("highTemp", high.joinToString(","))
            putString("lowTemp", low.joinToString(","))
            apply()
        }
        Log.d("nowTemp", nowTemp.toString())
        have2 = sp2.getInt("have", 0)
        if (have2 == 0) {
            with(sp2.edit()) {
                putString("cityName", nowCity.joinToString(","))
                putString("lowTemp", lowTemp.joinToString(","))
                putString("highTemp", highTemp.joinToString(","))
                putString("nowTemp", nowTemp.joinToString(","))
                putString("nowCould", dayCould.joinToString(","))
                putInt("have", 1)
                apply()
            }
        }

        with(sp2.edit()) {
            remove("nowTemp")
            remove("nowCould")
            remove("highTemp")
            remove("lowTemp")
            nowCity.forEachIndexed { i, city ->
                when (city) {
                    "台北市" -> {
                        newTemp.add(nowTemp[i])
                        newCloud.add(dayCould[i])
                        low.add(lowTemp[i])
                        high.add(highTemp[i])
                    }

                    "新北市" -> {
                        newTemp.add(nowTemp[i])
                        newCloud.add(dayCould[i])
                        low.add(lowTemp[i])
                        high.add(highTemp[i])
                    }

                    "台南市" -> {
                        newTemp.add(nowTemp[i])
                        newCloud.add(dayCould[i])
                        low.add(lowTemp[i])
                        high.add(highTemp[i])
                    }

                    "桃園市" -> {
                        newTemp.add(nowTemp[i])
                        newCloud.add(dayCould[i])
                        low.add(lowTemp[i])
                        high.add(highTemp[i])
                    }

                    "台中市" -> {
                        newTemp.add(nowTemp[i])
                        newCloud.add(dayCould[i])
                        low.add(lowTemp[i])
                        high.add(highTemp[i])
                    }
                }
            }
            putString("nowCould", newCloud.joinToString(","))
            putString("nowTemp", newTemp.joinToString(","))
            putString("highTemp", high.joinToString(","))
            putString("lowTemp", low.joinToString(","))
            apply()
        }

    }

    fun loadLocale() {
        val preferences = getSharedPreferences("state", Context.MODE_PRIVATE)
        val languageCode = preferences.getString("lan", "") ?: ""
        setLocale(languageCode)
    }

    fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }


    private inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return cityList.size
        }

        override fun getItem(position: Int): Fragment {
            return WeatherFragment(
                currentAQI,
                dayCould,
                highTemp,
                lowTemp,
                tenday,
                tendayCould,
                maxTemp,
                minTemp,
                nowCity,
                nowTemp,
                hourWeatherImage,
                hourWeather,
                hourTimeList,
                position,
                hourWeatherCount / cityList.size
            )
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupIndicators(count: Int) {
        val dotsLayout: LinearLayout = findViewById(R.id.dot_unselected)
        dotsLayout.removeAllViews()

        for (i in 0 until count) {
            val dot = ImageView(this)
            dot.setImageDrawable(resources.getDrawable(R.drawable.baseline_circle_24, null))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dot.layoutParams = params
            dotsLayout.addView(dot)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateIndicators(position: Int) {
        val dotsLayout: LinearLayout = findViewById(R.id.dot_unselected)
        for (i in 0 until dotsLayout.childCount) {
            val dot = dotsLayout.getChildAt(i) as ImageView
            dot.setImageDrawable(
                resources.getDrawable(
                    if (i == position) R.drawable.baseline_location_pin_24 else R.drawable.baseline_circle_24,
                    null
                )
            )
        }
    }

}
