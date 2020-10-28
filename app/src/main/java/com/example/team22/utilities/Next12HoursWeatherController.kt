package com.example.team22.utilities

import android.app.Activity
import com.example.team22.R
import com.example.team22.weather.Weather
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.roundToInt
/*
* Controller class to calculate how weather is visualized on recyclerview cards on explore and favourites-fragment.*/
class Next12HoursWeatherController(weather: Weather, val activity: Activity) {
    private val forecasts = weather.weatherForecasts



    /*@param interval: int from 0 - 5 where each number represents two hours in next 12 hours.
    * @param forWind is strictly to check if we are calculating backgroundcolor for windspeed or gust.
    * out: Returns background-color that should be displayed based on average of the two hours in interval in relationship to user preferences. */
    fun getBackgroundColor2HourAverage(userWindSpeedMin: Int,
                                       userWindSpeedMax: Int,
                                       userGustMin: Int,
                                       userGustMax: Int,
                                       interval: Int,
                                       forWind: Boolean
                                                ) : Int {
        var firstHourForecast = forecasts[0]
        var secondHourForecast = forecasts[1]
        val average: Double

        when (interval) {
            0 -> {
                firstHourForecast = forecasts[0]
                secondHourForecast = forecasts[1]
            }
            1 -> {
                firstHourForecast = forecasts[2]
                secondHourForecast = forecasts[3]
            }
            2 -> {
                firstHourForecast = forecasts[4]
                secondHourForecast = forecasts[5]
            }

            3 -> {
                firstHourForecast = forecasts[6]
                secondHourForecast = forecasts[7]
            }

            4 -> {
                firstHourForecast = forecasts[8]
                secondHourForecast = forecasts[9]
            }

            5 -> {
                firstHourForecast = forecasts[10]
                secondHourForecast = forecasts[11]
            }
        }
        return if (forWind) {
            average = (firstHourForecast.windSpeed + secondHourForecast.windSpeed) / 2
            getBackgroundColorWindSpeed(average, userWindSpeedMin, userWindSpeedMax)
        } else {
            average = (firstHourForecast.windSpeedOfGust!! + secondHourForecast.windSpeedOfGust!!) / 2
            getBackgroundColorWindGusts(average, userGustMin, userGustMax)

        }

    }
    /*Same interval-metrics as above. Calculates average temp between two hours in interval, and returns what temperature to show on LocationCard in recyclerview.*/
    fun get2HourInterTempAverage(interval: Int) : String {
        var firstHourForecast = forecasts[0]
        var secondHourForecast = forecasts[1]
        val average: Double
        when (interval) {
            0 -> {
                firstHourForecast = forecasts[0]
                secondHourForecast = forecasts[1]
            }
            1 -> {
                firstHourForecast = forecasts[2]
                secondHourForecast = forecasts[3]
            }
            2 -> {
                firstHourForecast = forecasts[4]
                secondHourForecast = forecasts[5]
            }

            3 -> {
                firstHourForecast = forecasts[6]
                secondHourForecast = forecasts[7]
            }

            4 -> {
                firstHourForecast = forecasts[8]
                secondHourForecast = forecasts[9]
            }

            5 -> {
                firstHourForecast = forecasts[10]
                secondHourForecast = forecasts[11]
            }
        }

        average = (firstHourForecast.temperature!! + secondHourForecast.temperature!!) / 2
        return "${average.roundToInt()} °"
    }
    //Returns symbolcode corresponding to first hour in interval from 0 - 5.
    fun getWeatherSymbolForHour(interval: Int) : String {
        var symbolCodeString = ""
        when (interval) {
            0 -> {
                symbolCodeString = forecasts[0].symbolCode

            }
            1 -> {
                symbolCodeString = forecasts[2].symbolCode

            }
            2 -> {
                symbolCodeString = forecasts[4].symbolCode
            }

            3 -> {

                symbolCodeString = forecasts[6].symbolCode

            }

            4 -> {

                symbolCodeString = forecasts[8].symbolCode

            }

            5 -> {

                symbolCodeString = forecasts[10].symbolCode

            }
        }
        return symbolCodeString
    }
    /*Returns string that represents time-interval between two hours. E.g if @param interval is 0, return value will be string representing next two hours from now: hour0 - hour2*/
    fun get2HourIntervalTimeText(interval : Int) : String {
        var firstHourString = ""
        var secondHourString = ""
        when (interval) {
            0 -> {
                firstHourString =
                    forecasts[0].dateAndTime!!
                        .format(DateTimeFormatter.ofPattern("kk"))

                secondHourString =
                    forecasts[2].dateAndTime!!
                        .format(DateTimeFormatter.ofPattern("kk"))
            }
            1 -> {

                firstHourString =
                    forecasts[2].dateAndTime!!
                        .format(DateTimeFormatter.ofPattern("kk"))

                secondHourString =
                    forecasts[4].dateAndTime!!
                        .format(DateTimeFormatter.ofPattern("kk"))
            }
            2 -> {

                firstHourString =
                    forecasts[4].dateAndTime!!
                        .format(DateTimeFormatter.ofPattern("kk"))

                secondHourString =
                    forecasts[6].dateAndTime!!
                        .format(DateTimeFormatter.ofPattern("kk"))
            }

            3 -> {

                firstHourString =
                    forecasts[6].dateAndTime!!
                        .format(DateTimeFormatter.ofPattern("kk"))

                secondHourString =
                    forecasts[8].dateAndTime!!
                        .format(DateTimeFormatter.ofPattern("kk"))
            }

            4 -> {

                firstHourString =
                    forecasts[8].dateAndTime!!
                        .format(DateTimeFormatter.ofPattern("kk"))

                secondHourString =
                    forecasts[10].dateAndTime!!
                        .format(DateTimeFormatter.ofPattern("kk"))
            }

            5 -> {

                firstHourString =
                    forecasts[10].dateAndTime!!
                        .format(DateTimeFormatter.ofPattern("kk"))

                secondHourString =
                    forecasts[12].dateAndTime!!
                        .format(DateTimeFormatter.ofPattern("kk"))
            }
        }
        return activity.getString(R.string.timePeriod, firstHourString, secondHourString)
    }
}
