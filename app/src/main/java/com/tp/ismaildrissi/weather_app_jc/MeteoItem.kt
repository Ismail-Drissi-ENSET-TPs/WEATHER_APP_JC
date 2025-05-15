package com.tp.ismaildrissi.weather_app_jc

data class MeteoItem(
    val temperature: Int,
    val tempMax: Int,
    val tempMin: Int,
    val pression: Int,
    val humidite: Int,
    val vent: Double,
    val meteo: String,
    val date: String
)