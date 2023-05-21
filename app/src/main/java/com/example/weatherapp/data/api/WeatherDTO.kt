package com.example.weatherapp.data.api

import com.google.gson.annotations.SerializedName

data class WeatherDTO(
    @SerializedName("location") val location: Location,
    @SerializedName("current") val current: Current,
)

data class Location(
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
)

data class Current(
    @SerializedName("condition") val condition: Condition,
    @SerializedName("temp_c") val temp_c: String
)

data class Condition(
    @SerializedName("text") val text: String,
    @SerializedName("icon") val icon: String,
)