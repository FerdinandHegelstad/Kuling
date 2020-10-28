package com.example.team22.utilities
import com.example.team22.weather.LocationForecastResponse
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import org.threeten.bp.LocalDateTime

fun makeHttpRequest(latitude: String,
                    longitude: String,
                    handler: LocationForecastResponse,
                    baseUrl: String = "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/2.0/") {

    try {
        val response = if (latitude == "test" && longitude == "test") {
                                                    Fuel.get(baseUrl).response()
                                                } else {
                                                    Fuel.get(baseUrl, listOf("lat" to latitude, "lon" to longitude)).response()
                                                }
        //uses dataclass LocationForecastResponse-instance as intermediary to correctly store response from get-request:
        if (response.second.isSuccessful) {
            handler.statusCode = response.second.statusCode
            handler.expires = convertExpiresHeaderToDateTime(response.second["Expires"].first())!!
            handler.responseBody = response.second.data.toString(Charsets.UTF_8)

        } else {
            handler.statusCode = response.second.statusCode
            handler.expires = LocalDateTime.MIN
            handler.responseBody = ""
        }
    } catch (exception: Exception) {
        handler.statusCode = -1
        handler.expires = LocalDateTime.MIN
        handler.responseBody = ""
    }
}

fun makeSimpleHttpRequest(baseUrl: String) : String {
    val response = Fuel.get(baseUrl).response()
    return response.second.data.toString(Charsets.UTF_8)
}

