package com.example.weatherapp.mapper

import com.example.weatherapp.data.model.WeatherModel
import com.example.weatherapp.ui.mvi.MainResult

object WeatherMapper {

    fun map(weatherModel: WeatherModel) = with(weatherModel) {
        MainResult(
            imageUrl = "https:" + imageUrl,
            message = "$country, $city: ${temperature}C"
        )
    }
}