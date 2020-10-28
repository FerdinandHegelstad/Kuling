

package com.example.team22.location

import android.os.Parcelable
import com.example.team22.weather.Weather
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDateTime
// Made parcelable so that object can be sent between activities:
@Parcelize
class KiteLocation(var name: String,
                   var id: Int,
                   var pictureUrl: String,
                   var latitude: Double,
                   var longitude: Double,
                   var distanceToUserInKm: Int = -1,
                   var weatherExpires: LocalDateTime = LocalDateTime.MIN,
                   var weather : Weather = Weather(mutableListOf()),
                   var county: String = "",
                   var municipality: String = "",
                   var isFavourite: Boolean = false
) : Parcelable {

    // KiteLocations are compared using their unique id
    override fun equals(other: Any?): Boolean {
        if (other is KiteLocation) {
            return id == other.id
        }
        return false
    }

    // Function is used to compare KiteLocation objects
    override fun hashCode(): Int {
        return id
    }

}
