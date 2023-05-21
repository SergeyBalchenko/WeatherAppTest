package com.example.weatherapp.data.impl

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.weatherapp.data.contract.LocationRepository
import com.example.weatherapp.data.error.LocationException
import com.example.weatherapp.data.model.Location
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AndroidLocationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun get(): Location = suspendCoroutine { continuation ->

        if (context.hasLocationPermission().not()) {
            continuation.resumeWithException(LocationException("Don't have right permissions"))
        }

        val locationsService = LocationServices.getFusedLocationProviderClient(context)

        locationsService.getCurrentLocation(PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

            override fun isCancellationRequested() = false
        }).addOnSuccessListener { loc ->
            if (loc == null)
                continuation.resumeWithException(LocationException("Location is null"))
            else {
                val lat = loc.latitude
                val lon = loc.longitude
                val location = Location(
                    longitude = lon,
                    latitude = lat
                )
                continuation.resume(location)
            }

        }
    }

    private fun Context.hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}