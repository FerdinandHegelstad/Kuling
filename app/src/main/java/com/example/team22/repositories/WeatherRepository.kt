package com.example.team22.repositories

import com.example.team22.location.KiteLocation
import com.example.team22.utilities.DataFetchException
import com.example.team22.utilities.makeHttpRequest
import com.example.team22.utilities.parseJSONResponseToWeatherForecasts
import com.example.team22.weather.LocationForecastResponse
import com.example.team22.weather.Weather
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class WeatherRepository {
    //fetch weather for given KiteLocation according to locations GPS-coordinates. Returns false if current weather-info is still recent.
    suspend fun fetchWeatherDataForLocation(location : KiteLocation) : Boolean {
        //checks if current weather-data has expired:
        if (location.weatherExpires.isAfter(LocalDateTime.now(ZoneId.of("Z")))) {
            return false
        }
        val handler = LocationForecastResponse()

       withContext(IO) {
           makeHttpRequest(
               latitude = location.latitude.toString(),
               longitude = location.longitude.toString(),
               handler = handler)
       }
        //error-handling, could not fetch weather-data:
        if (200 > handler.statusCode || handler.statusCode > 299) {
            location.weather = Weather(mutableListOf())
            throw DataFetchException("")
        } else {
            location.weatherExpires = handler.expires
            location.weather = Weather(
                parseJSONResponseToWeatherForecasts(handler.responseBody)
            )
        }
        return true
    }
}