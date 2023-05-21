package com.example.weatherapp.ui.mvi

sealed class MainUiIntent {
    object GetWeather : MainUiIntent()
    object PermissionsDeniedForever : MainUiIntent()
    object PermissionsRejected : MainUiIntent()
}
