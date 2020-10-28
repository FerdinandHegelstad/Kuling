package com.example.team22.utilities

import com.example.team22.R


//Checks current windspeed at given location in relationship to users windspeed preferences, and returns correct background-color.
fun getBackgroundColorWindSpeed(windSpeed: Double, windSpeedMin: Int, windSpeedMax: Int) : Int {
    return when {
        windSpeed < windSpeedMin -> R.color.colorIndicatorYellow
        windSpeed > windSpeedMax -> R.color.colorIndicatorRed
        else -> R.color.colorIndicatorGreen
    }
}
//Checks current windgust at given location in relationship to users windgust preferences, and returns correct background-color.
fun getBackgroundColorWindGusts(windSpeed: Double, gustMin: Int, gustMax: Int) : Int {
    return when {
        windSpeed < gustMin -> R.color.colorIndicatorYellow
        windSpeed > gustMax -> R.color.colorIndicatorRed
        else -> R.color.colorIndicatorGreen
    } 
}

