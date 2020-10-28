package com.example.team22.utilities

import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
/*Serves as intermediary to hold weather-data of location inside listAdapter of recyclerview showing cards with weather/location-info.*/
data class LocationCardDataClass(var windSpeedText: TextView,
                                 var gustSpeedText: TextView,
                                 var temperature: TextView,
                                 var symbol: AppCompatImageView,
                                 var hourText: TextView)
