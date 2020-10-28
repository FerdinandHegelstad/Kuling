package com.example.team22.utilities

import com.example.team22.weather.LocationForecastResponse
import org.junit.Assert.assertTrue
import org.junit.Test

class HTTPFunctionsTest{
    @Test
    fun `makeHttpRequest() performs expected behaviour`(){
        val lat = "60.190"
        val lon = "9.59"
        val handler = LocationForecastResponse()
        makeHttpRequest(lat, lon, handler)

        assertTrue(handler.statusCode in 200..299)
        assertTrue(handler.responseBody.isNotEmpty())

    }

    @Test
    fun `makeHttpRequest() handles 500 response`() {
        val baseUrl = "https://httpstat.us/500"
        val lat = "test"
        val lon = "test"
        val handler = LocationForecastResponse()

        makeHttpRequest(lat, lon, handler, baseUrl)
        assertTrue(handler.statusCode == 500)
        assertTrue(handler.responseBody == "")
    }

    @Test
    fun `makeSimpleHttpRequest() handles 500 response`() {
        val baseUrl = "https://httpstat.us/500"
        val responseString = makeSimpleHttpRequest(baseUrl)
        assertTrue(responseString == "500 Internal Server Error")
    }
}