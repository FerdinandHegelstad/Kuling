package com.example.team22.utilities

import android.app.Activity
import android.content.Intent
import com.example.team22.location.KiteLocation

fun startNewActivity(activity: Activity, activityClass: Class<*>, kiteLocation: KiteLocation?, favourites : List<KiteLocation>?) {
    val intent = Intent(activity, activityClass)
    intent.putExtra("location", kiteLocation)
    if (favourites != null) {
        intent.putExtra("favourites", favourites as ArrayList<KiteLocation>)
    }
    activity.startActivityForResult(intent, 5)
}

