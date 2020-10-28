package com.example.team22.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.team22.location.KiteLocation
import com.example.team22.utilities.makeSimpleHttpRequest
import com.example.team22.utilities.parseJSONResponseToKiteLocations
import com.example.team22.utilities.readFavouriteIdsFromFile
import com.example.team22.utilities.writeFavouriteIdsToFile
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class KiteLocationRepository(application: Application) {
    private var favouriteLocations: MutableList<KiteLocation> = mutableListOf()
    private var kiteLocations: List<KiteLocation> = listOf()
    private val favouritesFilename = "/favourites"
    private var context = application

    //Fetches predefined KiteLocations from JSON-url, and deserialized to KiteLocations-objects. Sets kiteLocations var to be list of KiteLocations
    suspend fun getLocations() : LiveData<List<KiteLocation>> {
        val locations: MutableLiveData<List<KiteLocation>> = MutableLiveData()

        withContext(IO) {
            val responseBody = makeSimpleHttpRequest("https://project-7a24e.firebaseapp.com/KiteLocations.json")
            //parse JSON-data to appropriate KiteLocation-objects with deserializer:
            kiteLocations = parseJSONResponseToKiteLocations(responseBody)
        }
        locations.value = kiteLocations
        return locations
    }
    //Gets list of KiteLocation ID's from file '/favourites', and returns MutableLiveData list of currently saved favourite-locations:
    fun getFavourites() : MutableLiveData<List<KiteLocation>> {
        val favourites: MutableLiveData<List<KiteLocation>> = MutableLiveData()
        val listOfFavouriteIds = readFavouriteIdsFromFile(context, favouritesFilename)
        kiteLocations.forEach { kiteLocation ->
            if (listOfFavouriteIds.contains(kiteLocation.id)) {
                kiteLocation.isFavourite = true
                favouriteLocations.add(kiteLocation)
            }
        }
        favourites.value = favouriteLocations
        return favourites
    }
    //saves current favourites to file as list of KiteLocation ID's.
    fun saveFavourites(favourites: MutableLiveData<List<KiteLocation>>) {
        writeFavouriteIdsToFile(context, favourites.value, favouritesFilename)
    }
}