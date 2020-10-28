package com.example.team22.weather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
// Dataclass for holding weatherforecasts for a given KiteLocation, made parcelable so that we can send object between activities:
@Parcelize
data class Weather(val weatherForecasts : MutableList<WeatherForecast>) : Parcelable

