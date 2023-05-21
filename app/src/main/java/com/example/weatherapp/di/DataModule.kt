package com.example.weatherapp.di

import com.example.weatherapp.data.contract.LocationRepository
import com.example.weatherapp.data.contract.NetworkRepository
import com.example.weatherapp.data.contract.WeatherRepository
import com.example.weatherapp.data.impl.AndroidLocationRepository
import com.example.weatherapp.data.impl.AndroidNetworkRepository
import com.example.weatherapp.data.impl.WeatherApiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindLocationRepository(androidLocationRepository: AndroidLocationRepository): LocationRepository

    @Binds
    fun bindNetworkRepository(androidNetworkRepository: AndroidNetworkRepository): NetworkRepository

    @Binds
    fun bindWeatherRepository(weatherApiRepository: WeatherApiRepository): WeatherRepository
}