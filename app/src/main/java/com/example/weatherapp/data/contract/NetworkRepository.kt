package com.example.weatherapp.data.contract

interface NetworkRepository {

    fun isNetworkAvailable(): Boolean
}