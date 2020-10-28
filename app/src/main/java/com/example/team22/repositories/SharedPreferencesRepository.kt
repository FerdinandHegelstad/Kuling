package com.example.team22.repositories

import android.app.Application
import com.example.team22.R

class SharedPreferencesRepository(application : Application) {

    private val FILE_NAME_PREFERENCES = application.getString(R.string.shared_preferences_preferences_file_name)
    private val preferences = application.getSharedPreferences(FILE_NAME_PREFERENCES, 0)

    //Saves given preference attributes to shared preferences file: com.example.team22.user_preferences
    fun savePreferences(windSpeedMin : Int, windSpeedMax : Int, gustMin : Int, gustMax : Int){
        with(preferences.edit()){
            putInt("wind_speed_min", windSpeedMin)
            putInt("wind_speed_max", windSpeedMax)
            putInt("gust_min", gustMin)
            putInt("gust_max", gustMax)
            putBoolean("first_run", false)
            commit()
        }
    }

    fun getWindSpeedMin() : Int{
        return preferences.getInt("wind_speed_min", 7)
    }

    fun getWindSpeedMax() : Int{
        return preferences.getInt("wind_speed_max", 10)
    }

    fun getGustMin() : Int{
        return preferences.getInt("gust_min", 9)
    }

    fun getGustMax() : Int{
        return preferences.getInt("gust_max", 12)
    }
    //checking if this is the first time running app on device:
    fun appRunningFirstTime() : Boolean{
        return preferences.getBoolean("first_run", true)
    }
    /*
    * When revoking permissions from app, Android security measure forces all activities in app to be destroyed/killed.
    * Therefore, when entering permissions-window from settings, value for key "from_settings" is set to true.
    * NavigationActivity checks this value upon creation. If its set to true, navigation-activity directly navigates to settings-fragment, and sets value to false.
    * User has an expectation to return to settings-page after changing permissions when pressing back-button. */
    fun setUserReturnsFromSettingsCheck(b : Boolean){
        with(preferences.edit()){
            putBoolean("from_settings", b)
            commit()
        }
    }

    fun getUserReturnsFromSettingsCheck() : Boolean{
        return preferences.getBoolean("from_settings", false)
    }
}