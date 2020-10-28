package com.example.team22.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.team22.repositories.SharedPreferencesRepository

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferencesRepo = SharedPreferencesRepository(application)

    //Checking if this is the first time running app on device:
    fun firstRun() : Boolean {
        return sharedPreferencesRepo.appRunningFirstTime()
    }
}
