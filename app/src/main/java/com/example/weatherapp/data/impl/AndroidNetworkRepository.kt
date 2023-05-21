package com.example.weatherapp.data.impl

import android.content.Context
import android.location.LocationManager
import com.example.weatherapp.data.contract.NetworkRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidNetworkRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : NetworkRepository {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override fun isNetworkAvailable() =
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}