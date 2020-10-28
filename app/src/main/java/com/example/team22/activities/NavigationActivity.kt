package com.example.team22.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.team22.R
import com.example.team22.utilities.OnClearFromRecentService
import com.example.team22.view_models.NavigationActivityViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.fragment_location_explore.*
import kotlinx.android.synthetic.main.fragment_location_favourites.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class NavigationActivity : AppCompatActivity() {
    // bottom navigation-bar listener. Changing to correct fragment according to what button is pressed on navigation bar.
    // Creates instance of fragment, if it does not already exist.
    private val mOnNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {item ->
    when(item.itemId) {
        R.id.menu_explore -> {
            val tag = "explore"
            if (!isFragmentActive(tag)) {
                changeFragment(LocationExploreFragment(), tag)
            } else {
                val fragment = getFragment(tag) as LocationExploreFragment
                // search-field in explore should be empty when changing to explore:
                fragment.locationSearchFragment_editText_searchField.editableText.clear()
                // scroll to top:
                fragment.locationSearchFragment_recyclerView_locations.scrollToPosition(0)
            }
            return@OnNavigationItemSelectedListener true
        }
        R.id.menu_favourites -> {
            val tag = "favourites"
            if (!isFragmentActive(tag)) {
                changeFragment(LocationFavouritesFragment(), tag)
            } else {
                val fragment = getFragment(tag) as LocationFavouritesFragment
                // scroll to top:
                fragment.locationFavouritesFragment_recyclerView_locations.scrollToPosition(0)
            }
            return@OnNavigationItemSelectedListener true
        }
        R.id.menu_map -> {
            val tag = "map"
            if (!isFragmentActive(tag)) {
                changeFragment(LocationMapFragment(), tag)
            }
            return@OnNavigationItemSelectedListener true
        }
        R.id.menu_settings -> {
            val tag = "settings"
            if (!isFragmentActive(tag)) {
                changeFragment(LocationSettingsFragment(), tag)
            }
            return@OnNavigationItemSelectedListener true
        }

    }
        false
    }

    lateinit var viewModel: NavigationActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)

        //starting service that will execute when app is closed abruptly:
        startService(Intent(baseContext, OnClearFromRecentService::class.java))

        viewModel = ViewModelProvider(this).get(NavigationActivityViewModel::class.java)
        /*
         When user revokes permission(location) all activities are stopped. When pressing back button
         from android application settings menu, user should be redirected to same page as before
         permission was revoked(settings)
         This checks if user is NOT returning from settings:*/
        if (!viewModel.getUserHasReturnedFromSettingsCheck()) {
            if (!viewModel.hasUserLocationPermission()) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

        setContentView(R.layout.activity_navigation)
        bottom_Navigation_Bar.visibility = View.INVISIBLE
        CoroutineScope(Main).launch {
            navigation_activity_loading_text.text = getString(R.string.navigation_activity_loading_location_text)
            //fetch location-data for internet:
            viewModel.generateLocations()
            navigation_activity_loading_text.text =  getString(R.string.navigation_activity_loading_distance_text)
            //set distances from user to all locations:
            viewModel.setDistanceFromUserToAllLocations()
            //Checking if user just returned from app-settings page or not:
            if (viewModel.getUserHasReturnedFromSettingsCheck()) {
                //set 'flag' to false, and redirect to settings:
                viewModel.setUserHasReturnedFromSettingsCheck(false)
                changeFragment(LocationSettingsFragment(), "settings")
            } else {
                // if user has not returned from settings, redirect to automatic start-view, explore:
                changeFragment(LocationExploreFragment(), "explore")
            }
            // make bottom navigation bar visible after data-loading is completed:
            bottom_Navigation_Bar.visibility = View.VISIBLE
            navigation_activity_loading_text.visibility = View.INVISIBLE
            hideProgressbar()

        }
        bottom_Navigation_Bar.setOnNavigationItemSelectedListener(mOnNavItemSelectedListener)
    }

    override fun onStop() {
        super.onStop()
        // save to file when activity is stopped:
        viewModel.saveFavouriteLocations()
    }
    // when receiving data from LocationViewer:
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 5) {
            if (resultCode == Activity.RESULT_OK) {
                // edit location via viewmodel, to update favourite-attribute if necessary for location user just inspected:
                viewModel.editLocation(data!!.getParcelableExtra("location")!!)
            }
        }
    }

    private fun hideProgressbar() {
        navigation_activity_progress_bar.visibility = View.INVISIBLE

    }

    private fun changeFragment(fragment: Fragment, tag: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, tag)
        fragmentTransaction.commit()
    }
    //checking if fragment with tag @param tag is already active or not:
    private fun isFragmentActive(tag: String) : Boolean {
        val activeFragments =  supportFragmentManager.fragments
        for (fragment in activeFragments) {
            if (fragment.tag == tag) {
                return true
            }
        }
        return false
    }

    private fun getFragment(tag: String) : Fragment? {
        val activeFragments =  supportFragmentManager.fragments
        for (fragment in activeFragments) {
            if (fragment.tag == tag) {
                return fragment
            }
        }
        return null
    }

    override fun onResume() {
        super.onResume()
        /*If user redirects from settings, and location-permissions are turned on, distance from user
        to all locations must be visible: */
        if(viewModel.getLocations().value != null){
            CoroutineScope(Main).launch {
                viewModel.setDistanceFromUserToAllLocations()
            }
        }
    }
}
