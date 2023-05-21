package com.example.weatherapp.mapper

import com.example.weatherapp.data.api.WeatherDTO
import com.example.weatherapp.data.model.WeatherModel

fun WeatherDTO.mapToWeather(): WeatherModel {
    return WeatherModel(
        country = location.country,
        city = location.name,
        temperature = current.temp_c,
        imageUrl = current.condition.icon,
    )
}