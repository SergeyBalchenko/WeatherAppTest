package com.example.weatherapp.data.contract

import com.example.weatherapp.data.error.LocationException
import com.example.weatherapp.data.model.Location
import retrofit2.HttpException

interface LocationRepository {

    /**
     * Get current user location
     * @throws LocationException if the request was fail for any reasons
     */
    suspend fun get(): Location
}