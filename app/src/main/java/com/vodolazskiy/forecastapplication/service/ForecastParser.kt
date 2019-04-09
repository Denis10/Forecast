package com.vodolazskiy.forecastapplication.service

import com.vodolazskiy.forecastapplication.domain.entity.City
import com.vodolazskiy.forecastapplication.domain.entity.Forecast
import com.vodolazskiy.forecastapplication.domain.entity.ForecastItem
import com.vodolazskiy.forecastapplication.domain.entity.MainForecast
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

object ForecastParser {

    fun parse(forecastJson: JSONObject): Forecast {
        val cityJson = forecastJson.getJSONObject("city")
        val city = City(
            id = cityJson.getString("id"),
            name = cityJson.getString("name")
        )
        val forecastItems: MutableList<ForecastItem> = ArrayList()
        val forecastsJson = forecastJson.getJSONArray("list")
        var i = 0
        while (i < forecastsJson.length()) {
            val forecastItemJson = forecastsJson.getJSONObject(i)
            val date = Date(forecastItemJson.getInt("dt") * 1000L)
            val mainJson = forecastItemJson.getJSONObject("main")
            val mainForecast = MainForecast(mainJson.getDouble("temp"))
            forecastItems.add(ForecastItem(date, mainForecast))
            i++
        }

        return Forecast(city = city, forecasts = forecastItems)
    }
}
