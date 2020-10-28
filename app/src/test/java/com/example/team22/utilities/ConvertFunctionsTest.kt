package com.example.team22.utilities

import com.example.team22.weather.LocationForecastResponse
import org.junit.Assert.*
import org.junit.Test

class ConvertFunctionsTest{

    @Test
    fun `convertStringToLocalDateTime() returns expected values`() {
        assertNotNull(convertStringToLocalDateTime("2020-03-24T07:58:46Z"))
        assertNotNull(convertStringToLocalDateTime("2023-12-24T23:58:46Z"))
        assertNull(convertStringToLocalDateTime("aaaaaaaaa"))
        assertNull(convertStringToLocalDateTime("1254-222-24T23:12:46X"))
        assertNull(convertStringToLocalDateTime("2021-01-333T22:51:12Z"))
        assertNull(convertStringToLocalDateTime("2021-01-99T22:51:12Z"))
        assertNull(convertStringToLocalDateTime("2021-01-12T223:51:12Z"))
        assertNull(convertStringToLocalDateTime("2021-01-12T23:151:12Z"))
        assertNull(convertStringToLocalDateTime("2021-01-12T23:15:12222Z"))
    }

    @Test
    fun `convertExpiresHeaderToDateTime() returns expected values`() {
        assertNotNull(convertExpiresHeaderToDateTime("Wed, 21 Oct 2015 07:28:00 GMT"))
        assertNotNull(convertExpiresHeaderToDateTime("Thu, 26 Mar 2020 07:14:00 GMT"))
        assertNull(convertExpiresHeaderToDateTime("aaaaaaaaa"))
        assertNull(convertExpiresHeaderToDateTime("[Tue, 21 Mars 2020 07:14:00 GMT]"))
        assertNull(convertExpiresHeaderToDateTime("sTue, 21 Oct 2020 07:14:00 GMT"))
        assertNull(convertExpiresHeaderToDateTime("2021-01-99T22:51:12Z"))
        assertNull(convertExpiresHeaderToDateTime("2021-01-12T223:51:12Z"))
        assertNull(convertExpiresHeaderToDateTime("2021-01-12T23:151:12Z"))
        assertNull(convertExpiresHeaderToDateTime("2021-01-12T23:15:12222Z"))
    }


    @Test
    fun `parseJSONResponseToWeatherForecasts() empty input`() {
        val emptyJson = ""
        val forecastList =
            parseJSONResponseToWeatherForecasts(emptyJson)
        assertTrue(forecastList.isEmpty())
    }

    @Test
    fun `parseJSONResponseToWeatherForecasts() empty json`() {
        val emptyJson = "{}"
        val forecastList =
            parseJSONResponseToWeatherForecasts(emptyJson)
        assertTrue(forecastList.isEmpty())
    }

    @Test
    fun `parseJSONResponseToWeatherForecasts() with wrong json`() {
        val wrongJsonString = "[ffd{{}}]"
        val forecastList =
            parseJSONResponseToWeatherForecasts(wrongJsonString)
        assertTrue(forecastList.isEmpty())
    }

    @Test
    fun `parseJSONResponseToKiteLocations() empty input`() {
        val emptyJson = ""
        val kiteLocations =
            parseJSONResponseToKiteLocations(emptyJson)
        assertTrue(kiteLocations.isEmpty())
    }

    @Test
    fun `parseJSONResponseToKiteLocations() empty json`() {
        val emptyJson = "{}"
        val kiteLocations =
            parseJSONResponseToKiteLocations(emptyJson)
        assertTrue(kiteLocations.isEmpty())
    }

    @Test
    fun `parseJSONResponseToKiteLocations() with wrong json`() {
        val wrongJsonString = "[ffd{{}}]"
        val kiteLocations =
            parseJSONResponseToKiteLocations(wrongJsonString)
        assertTrue(kiteLocations.isEmpty())
    }


}