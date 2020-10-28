package com.example.team22.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.team22.location.KiteLocation
import com.example.team22.repositories.KiteLocationRepository
import com.example.team22.repositories.SharedPreferencesRepository
import com.example.team22.repositories.WeatherRepository
import com.example.team22.weather.Weather
import com.example.team22.weather.WeatherForecast
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime


class LocationViewerViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherRepository = WeatherRepository()
    private var weatherData: MutableLiveData<Weather> = MutableLiveData()
    private var isUpdating: MutableLiveData<Boolean> = MutableLiveData()
    private var sharedPreferencesRepo = SharedPreferencesRepository(application)
    private var locationRepo = KiteLocationRepository(application)
    lateinit var kiteLocation: KiteLocation
    var longTermForecast: MutableList<MutableList<WeatherForecast>> = mutableListOf()
    private var favourites : MutableLiveData<List<KiteLocation>> = MutableLiveData()

    fun setFavourites(favouriteList: List<KiteLocation>) {
        favourites.value = favouriteList
    }

    fun getUserMinWindSpeed() : Int {
        return sharedPreferencesRepo.getWindSpeedMin()
    }

    fun saveFavourites(){
        locationRepo.saveFavourites(favourites)
    }
    // When favourite-button is pressed, function edits the list of favourites to remove/add current location.
    fun editFavouriteForCurrentLocation() {
        val favouritesList: MutableList<KiteLocation> = favourites.value as MutableList<KiteLocation>

        kiteLocation.isFavourite = !kiteLocation.isFavourite
        if (favouritesList.contains(kiteLocation)) {
            favouritesList.remove(kiteLocation)
        } else {
            favouritesList.add(kiteLocation)
        }
        //update livedata container with favourites:
        favourites.postValue(favouritesList)

    }

    fun getUserMaxWindSpeed() : Int {
        return sharedPreferencesRepo.getWindSpeedMax()
    }

    fun getUserMaxGust() : Int {
        return sharedPreferencesRepo.getGustMax()
    }

    fun getUserMinGust() : Int {
        return sharedPreferencesRepo.getGustMin()
    }

    // Adding location that is viewed in activity:
    fun addKiteLocation(location: KiteLocation?) {
        if (location != null) {
            kiteLocation = location
            weatherData.value = kiteLocation.weather
        }

    }

    @Throws(Exception::class)
    suspend fun generateWeather() {
        isUpdating.value = true
        //using repository to fetch weather:
        if (weatherRepository.fetchWeatherDataForLocation(kiteLocation)) {
            weatherData.postValue(kiteLocation.weather)
        }
        isUpdating.value = false
    }

    fun getWeather() : LiveData<Weather>  {
        return weatherData
    }

    fun getIsUpdating() : LiveData<Boolean> {
        return isUpdating
    }

    // Function finds the forecast for specific intervals to be shown in the long term forecast
    fun getLongTermForecastsList(weather: Weather) {
        val longTerm = mutableListOf<MutableList<WeatherForecast>>()
        val localMidnight = ZonedDateTime.now(ZoneId.systemDefault()).withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1).minusNanos(1)
        var listOfForecastsPerDay = mutableListOf<WeatherForecast>()
        weather.weatherForecasts.forEach {weatherForecast ->
            val dateAndTimeInUTC = weatherForecast.dateAndTime?.atZone(ZoneId.systemDefault())?.withZoneSameInstant(
                ZoneId.of("Z"))
            if (dateAndTimeInUTC != null) {
                if (dateAndTimeInUTC.isAfter(localMidnight) && longTerm.size < 7) {
                    if (dateAndTimeInUTC.hour == 0) {
                        if (listOfForecastsPerDay.isNotEmpty()) {
                            listOfForecastsPerDay = mutableListOf()
                        }
                        listOfForecastsPerDay.add(weatherForecast)
                    } else if (dateAndTimeInUTC.hour == 6 || dateAndTimeInUTC.hour == 12) {
                        listOfForecastsPerDay.add(weatherForecast)
                    } else if (dateAndTimeInUTC.hour == 18) {
                        listOfForecastsPerDay.add(weatherForecast)
                        longTerm.add(listOfForecastsPerDay)
                        listOfForecastsPerDay = mutableListOf()
                    }
                }
            }
        }
        longTermForecast = longTerm
    }

}
