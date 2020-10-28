package com.example.team22.view_models

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.example.team22.repositories.SharedPreferencesRepository

class UserPreferencesActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var sharedPreferencesRep = SharedPreferencesRepository(application)
    var windSpeedMin = sharedPreferencesRep.getWindSpeedMin()
    var windSpeedMax = sharedPreferencesRep.getWindSpeedMax()
    var gustMin = sharedPreferencesRep.getGustMin()
    var gustMax = sharedPreferencesRep.getGustMax()

    //checking if app has permission to record device location:
    fun appHasUserLocationPermission() : Boolean {
        return ActivityCompat
            .checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED
    }
    //using sharedPreferencesRepository to save user preferences to shared preferences:
    fun saveUserPreferences() {
        sharedPreferencesRep.savePreferences(windSpeedMin, windSpeedMax, gustMin, gustMax)
    }

    fun getWindSpeedOverviewText() : String {
        return "$windSpeedMin - $windSpeedMax m/s"
    }

    fun getGustOverviewText() : String {
        return "$gustMin - $gustMax m/s"
    }
    //setting or clearing value to check if user has come from settings-fragment:
    fun setUserReturnsFromSettingsCheck(b : Boolean){
        sharedPreferencesRep.setUserReturnsFromSettingsCheck(b)
    }

}
