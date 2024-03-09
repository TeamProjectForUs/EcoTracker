package com.example.greenapp.modules.AirQuality

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat
import com.example.greenapp.R
import com.example.greenapp.network.HttpPostClient
import com.google.gson.Gson
import im.delight.android.location.SimpleLocation
import java.util.Date

data class Color(
    val red: Double,
    val green: Double,
    val blue: Double,
)

data class Location(
    val latitude: Double,
    val longitude: Double,
)

data class AirQualityRequest(
    val location: Location,
)

data class Index(
    val code: String,
    val displayName: String,
    val aqi: Double,
    val aqiDisplay: String,
    val color: Color,
    val category: String,
    val dominantPollutant: String,
)

data class AirQuality(
    val date: Date,
    val regionCode: String,
    val indexes: List<Index>,
)

class AirQualityAPI(val httpPostClient: HttpPostClient = HttpPostClient()) {

    private fun url(key: String): String {
        return "https://airquality.googleapis.com/v1/currentConditions:lookup?key=$key"
    }

    suspend fun getCurrentPositionAirQuality(context: Context): AirQuality? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PERMISSION_GRANTED
        ) {

            val key = context.getString(R.string.api_key)
            val simpleLocation = SimpleLocation(context)
            simpleLocation.beginUpdates()

            val lat = simpleLocation.latitude
            val lng = simpleLocation.longitude
            val request = AirQualityRequest(Location(lat, lng))
            val body = Gson().toJson(request)
            return httpPostClient.post<AirQuality>(url(key), body)
        }
        return null
    }
}