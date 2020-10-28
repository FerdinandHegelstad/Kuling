package com.example.team22.utilities


import com.example.team22.location.KiteLocation
import com.example.team22.weather.WeatherForecast
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatter.RFC_1123_DATE_TIME
import org.threeten.bp.format.DateTimeParseException


/*
    * In: string-representation of date/time from location-forecast API
    * Out: LocalDateTime-object from @timestamp
    * NB: Not converted to specific timezone, converted to UTC.
    * */
fun convertStringToLocalDateTime(timestamp: String): ZonedDateTime? {
    return try {
        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")
        ZonedDateTime.parse(timestamp, format)
    } catch (e : DateTimeParseException) {
        null
    }

}
/*
* Same functionality as above, converts expires-header timestamp to LocalDateTime-object:*/
fun convertExpiresHeaderToDateTime(timestamp: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(timestamp, RFC_1123_DATE_TIME)
    } catch (e : DateTimeParseException) {
        null
    }
}

/*in: JSON-responseBody from location-forecast API.
    * out: MutableList<WeatherForecast> objects with date/time, fog, windSpeed, windDirection, windSpeedOfGust, and symbolCode representing weather for the next hour
    * returns empty mutableListOf if responseBody is empty.
*/
fun parseJSONResponseToWeatherForecasts(responseBody: String): MutableList<WeatherForecast> {

    val weatherForecastList = mutableListOf<WeatherForecast>()

    if (responseBody.isEmpty()) {
        return weatherForecastList
    }

    try {
        //array of JSON-objects containing relevant time-intervals and weather-info:
        val timeSeriesArray =
            JSONObject(responseBody)
                .getJSONObject("properties")
                .getJSONArray("timeseries")


        //for all timeSeries-objects..
        for (i in 0 until timeSeriesArray.length()) {
            val timeSeriesObject = (timeSeriesArray.get(i) as JSONObject)

            val dateAndTimeString = timeSeriesObject.getString("time")
            val zonedDateTime: ZonedDateTime? = convertStringToLocalDateTime(dateAndTimeString)
            val dateAndTime = zonedDateTime?.withZoneSameInstant(ZoneId.systemDefault())?.toLocalDateTime()
            //nesting to reach right object:
            val instantDetails =
                timeSeriesObject.getJSONObject("data")
                    .getJSONObject("instant")
                    .getJSONObject("details")

            //fog_area_fraction not available in all timeSeries-objects:
            var fogAreaFraction: Double? = null
            if (instantDetails.has("fog_area_fraction")) {
                fogAreaFraction = instantDetails.getDouble("fog_area_fraction")
            }

            val windSpeed = instantDetails.getDouble("wind_speed")
            val windFromDirection = instantDetails.getDouble("wind_from_direction")

            var temperature: Double? = null
            if (instantDetails.has("air_temperature")) {
                temperature = instantDetails.getDouble("air_temperature")
            }

            //wind_speed_of_gust not available in all timeSeries-objects:
            var windSpeedOfGust : Double? = null
            if (instantDetails.has("wind_speed_of_gust")) {
                windSpeedOfGust = instantDetails.getDouble("wind_speed_of_gust")
            }
            //symbol_code not available in all timeSeries-objects:
            var symbolCode = "N/A"
            if (timeSeriesObject.getJSONObject("data").has("next_1_hours")) {
                symbolCode =
                    timeSeriesObject
                        .getJSONObject("data")
                        .getJSONObject("next_1_hours")
                        .getJSONObject("summary")
                        .getString("symbol_code")
            }
            var symbolCodeNextSixHours = "N/A"
            if (timeSeriesObject.getJSONObject("data").has("next_6_hours")) {
                symbolCodeNextSixHours =
                    timeSeriesObject
                        .getJSONObject("data")
                        .getJSONObject("next_6_hours")
                        .getJSONObject("summary")
                        .getString("symbol_code")
            }
            //add object to list of weatherForecasts:
            weatherForecastList.add(
                WeatherForecast(
                    dateAndTime,
                    fogAreaFraction,
                    windSpeed,
                    windFromDirection,
                    windSpeedOfGust,
                    symbolCode,
                    temperature,
                    symbolCodeNextSixHours)
            )
        }
    } catch (e : JSONException) {
        return mutableListOf()
    }
    return weatherForecastList
}


fun parseJSONResponseToKiteLocations(responseBody: String): MutableList<KiteLocation> {
    val kiteLocationList = mutableListOf<KiteLocation>()
    if (responseBody.isNotEmpty()) {
        try {
            val kiteLocationArray = JSONArray(responseBody)

            for (i in 0 until kiteLocationArray.length()) {
                //serialize as KiteLocation object:
                val kiteLocationObject = (kiteLocationArray.get(i) as JSONObject)
                val kiteLocationName = kiteLocationObject.getString("name")
                val kiteLocationId = kiteLocationObject.getInt("id")
                val kiteLocationPictureUrl = kiteLocationObject.getString("pictureUrl")
                val kiteLocationLatitude = kiteLocationObject.getDouble("latitude")
                val kiteLocationLongitude = kiteLocationObject.getDouble("longitude")
                val kiteLocationCounty = kiteLocationObject.getString("county")
                val kiteLocationMunicipality = kiteLocationObject.getString("municipality")

                kiteLocationList.add(
                    KiteLocation(
                        name = kiteLocationName,
                        id = kiteLocationId,
                        pictureUrl = kiteLocationPictureUrl,
                        latitude = kiteLocationLatitude,
                        longitude = kiteLocationLongitude,
                        county = kiteLocationCounty,
                        municipality = kiteLocationMunicipality
                    )
                )
            }
        } catch (jsonException : JSONException) {
            return mutableListOf()
        }
    }

    return kiteLocationList

}
