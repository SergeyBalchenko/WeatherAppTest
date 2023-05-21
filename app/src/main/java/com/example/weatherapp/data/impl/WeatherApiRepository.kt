package com.example.weatherapp.data.impl

import com.example.weatherapp.data.api.WeatherApi
import com.example.weatherapp.data.contract.WeatherRepository
import com.example.weatherapp.data.model.Location
import com.example.weatherapp.data.model.WeatherModel
import com.example.weatherapp.mapper.mapToWeather
import javax.inject.Inject

class WeatherApiRepository @Inject constructor(
    private val weatherApi: WeatherApi,
) : WeatherRepository {

    override suspend fun getWeather(location: Location): WeatherModel {
        return weatherApi.getCurrent(
            query = "${location.latitude},${location.longitude}"
        ).mapToWeather()
    }
}