package com.example.team22.view_models


import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.team22.location.KiteLocation
import com.example.team22.repositories.KiteLocationRepository
import com.example.team22.repositories.SharedPreferencesRepository
import com.example.team22.repositories.WeatherRepository
import com.example.team22.utilities.DataFetchException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class NavigationActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val kiteLocationRepo = KiteLocationRepository(application)
    private val weatherRepo = WeatherRepository()
    private var kiteLocations: MutableLiveData<List<KiteLocation>> = MutableLiveData()
    private var sharedPreferencesRepo = SharedPreferencesRepository(application)
    private var favourites: MutableLiveData<List<KiteLocation>> = MutableLiveData()
    private var isLocationDistancesLoading: MutableLiveData<Boolean> = MutableLiveData()
    private var emptySearch: MutableLiveData<Boolean> = MutableLiveData()
    private var searchInProgress: MutableLiveData<Boolean> = MutableLiveData()
    private var searchString: String = ""
    private var dataFetchSuccess: MutableLiveData<Boolean> = MutableLiveData()

    private val context = getApplication<Application>().applicationContext
    //using KiteLocationRepository to fetch location-objects from http-get:
    suspend fun generateLocations() {
        kiteLocations = kiteLocationRepo.getLocations() as MutableLiveData<List<KiteLocation>>
        favourites = kiteLocationRepo.getFavourites()
    }

    fun getLocations() : LiveData<List<KiteLocation>> {
        return kiteLocations
    }

    fun getFavourites() : LiveData<List<KiteLocation>> {
        return favourites
    }

    fun getIsLocationDistancesLoading() : LiveData<Boolean> {
        return isLocationDistancesLoading
    }

    fun getDataFetchSuccess() : LiveData<Boolean> {
        return dataFetchSuccess
    }

    fun getEmptySearch() : LiveData<Boolean> {
        return emptySearch
    }

    fun getSearchInProgress() : LiveData<Boolean> {
        return searchInProgress
    }

    fun getSearchString() : String {
        return searchString
    }
    //when favourites-icon is pressed either in explore or favourites, function adds/removes location from current favourites-list:
    fun editFavourite(location: KiteLocation) {
        val favouritesList: MutableList<KiteLocation> = favourites.value as MutableList<KiteLocation>
        location.isFavourite = !location.isFavourite
        if (favouritesList.contains(location)) {
            favouritesList.remove(location)
        } else {
            favouritesList.add(location)
        }
        favourites.postValue(favouritesList)
    }
    //using repository to save current list of favourite Kiting locations to file:
    fun saveFavouriteLocations() {
        kiteLocationRepo.saveFavourites(favourites)
    }
    //Checking if user has granted permission to record device location:
    fun hasUserLocationPermission() : Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    //calculating distance from users device to all KiteLocations:
    fun setDistanceFromUserToAllLocations() {
        if (hasUserLocationPermission()) {
            isLocationDistancesLoading.postValue(true)
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val userLocation: Location? = task.result
                //if last location is not present on device, we have to get current location:
                if (userLocation == null) {
                    getNewLocation()
                } else { //setting distances..
                    setDistances(userLocation)
                }
            }
        }
    }

    private fun setDistances(userLocation : Location) {
        if (kiteLocations.value != null) {
            //for each KiteLocation, set distance from user to location..
            for (location in kiteLocations.value!!) {
                val spotLocation = Location("")
                spotLocation.latitude = location.latitude
                spotLocation.longitude = location.longitude
                val distance = (userLocation.distanceTo(spotLocation) / 1000).toInt()
                location.distanceToUserInKm = distance
            }
        }
        isLocationDistancesLoading.postValue(false)
    }
    //if for some reason current location of device is null, we have to get current location:
    private fun getNewLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.numUpdates = 1
        locationRequest.interval = 1
        locationRequest.fastestInterval = 1

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastUserLocation = locationResult.lastLocation
            setDistances(lastUserLocation)
        }
    }
    //changes kiteLocations livedata variable to contain list of kiteLocations sorted by proximity:
    fun sortKiteLocationsByProximity() {
            val newList : MutableList<KiteLocation>? = kiteLocations.value as? MutableList<KiteLocation>
            newList?.sortBy { it.distanceToUserInKm }
            kiteLocations.postValue(newList)
    }

    fun sortKiteLocationsByAlphabeticalOrder() {
        val newList : MutableList<KiteLocation>? = kiteLocations.value as? MutableList<KiteLocation>
        newList?.sortBy { it.name }
        kiteLocations.postValue(newList)
    }

    fun setEmptySearch(boolean: Boolean, searchText: String) {
        emptySearch.postValue(boolean)
        searchString = searchText
    }

    fun setSearchInProgress(boolean: Boolean) {
        searchInProgress.postValue(boolean)
    }
    /**
     * When returning from LocationViewerActivity, this method is called to check if the
     * kitelocation had been made a favourite or removed as a favourite. The favourite list is
     * updated accordingly
    */
    fun editLocation(kiteLocation: KiteLocation) {
        val locations: MutableList<KiteLocation>? = kiteLocations.value as? MutableList
        if (locations?.indexOf(kiteLocation) != -1) {
            locations?.set(locations.indexOf(kiteLocation), kiteLocation)
            kiteLocations.postValue(locations as List<KiteLocation>)

            val favouritesList: MutableList<KiteLocation> = favourites.value as MutableList<KiteLocation>
            if (kiteLocation.isFavourite) {
                if (!favouritesList.contains(kiteLocation)) {
                    favouritesList.add(kiteLocation)
                    favourites.postValue(favouritesList)
                }
            } else {
                if (favouritesList.contains(kiteLocation)) {
                    favouritesList.remove(kiteLocation)
                    favourites.postValue(favouritesList)
                }
            }
        }

    }

    @Throws(DataFetchException::class)
    suspend fun setWeatherForLocation(kiteLocation: KiteLocation) : Boolean {
       return weatherRepo.fetchWeatherDataForLocation(kiteLocation)
    }

    fun getUserMinWindSpeed() : Int {
        return sharedPreferencesRepo.getWindSpeedMin()
    }

    fun getUserMaxWindSpeed() : Int {
        return sharedPreferencesRepo.getWindSpeedMax()
    }

    fun getUserMinGust() : Int {
        return sharedPreferencesRepo.getGustMin()
    }

    fun getUserMaxGust() : Int {
        return sharedPreferencesRepo.getGustMax()
    }

    fun getUserHasReturnedFromSettingsCheck() : Boolean{
        return sharedPreferencesRepo.getUserReturnsFromSettingsCheck()
    }

    fun setUserHasReturnedFromSettingsCheck(b : Boolean){
        sharedPreferencesRepo.setUserReturnsFromSettingsCheck(b)
    }

    fun setWeatherDataSuccess(boolean: Boolean) {
        dataFetchSuccess.postValue(boolean)
    }
    /*method is called when application process is destroyed on device.
    Method sets key "from_settings" value to false, so that when user opens app next time,
    they will not get redirected to settings automatically. */
    override fun onCleared() {
        super.onCleared()
        sharedPreferencesRepo.setUserReturnsFromSettingsCheck(false)
    }


}
