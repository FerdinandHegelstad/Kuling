package com.example.team22.weather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDateTime

/*Certain attributes are not present in every timeSeries-object in the API-call:
* @fogAreaFraction and @windSpeedOfGust is null if attribute value is not present in API-call.
* @symbolCode is "" if attribute value is not present in API-call
* Made parceable so that object can be sent between activities*/
@Parcelize
data class WeatherForecast (val dateAndTime : LocalDateTime?,
                            val fogAreaFraction : Double?,
                            val windSpeed : Double = 0.0,
                            val windFromDirection : Double,
                            val windSpeedOfGust : Double?,
                            val symbolCode : String = "",
                            val temperature: Double?,
                            val symbolCodeNextSixHours: String = ""
                            ) : Parcelable