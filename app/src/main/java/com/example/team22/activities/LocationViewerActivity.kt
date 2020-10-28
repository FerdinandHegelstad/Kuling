package com.example.team22.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.team22.R
import com.example.team22.location.KiteLocation
import com.example.team22.utilities.DataFetchException
import com.example.team22.view_models.LocationViewerViewModel
import com.example.team22.weather.LongTermForecastListAdapter
import com.example.team22.weather.WeatherHourByHourListAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_location_viewer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter


class LocationViewerActivity : AppCompatActivity() {
    private lateinit var hourByHourListAdapter: WeatherHourByHourListAdapter
    private lateinit var longTermForecastListAdapter: LongTermForecastListAdapter
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    private lateinit var viewModel: LocationViewerViewModel
    private lateinit var scrollView: ScrollView
    private lateinit var mapOverlayImage: ImageView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContentView(R.layout.activity_location_viewer)
        location_viewer_activity_progress_bar_hbh.visibility = View.VISIBLE
        scrollView = location_viewer_scroll_view
        mapOverlayImage = location_viewer_map_overlay

        viewModel = ViewModelProvider(this)
            .get(LocationViewerViewModel::class.java)
        // getting KiteLocation sent from navigation activity that is currently inspected, and adding to viewModel:
        viewModel.addKiteLocation(intent.getParcelableExtra("location"))

        // getting currently updated favouriteslist, so that favourite-icon on Location can be correct:
        val favouritesList: ArrayList<KiteLocation>? = intent.getParcelableArrayListExtra("favourites")
        viewModel.setFavourites(favouritesList as List<KiteLocation>)
        setFavouriteIcon()

        // set values for Views:
        location_viewer_activity_location_title.text = viewModel.kiteLocation.name
        val locationText = "${viewModel.kiteLocation.municipality}, ${viewModel.kiteLocation.county}"
        location_viewer_text_county.text = locationText
        
        CoroutineScope(Main).launch {
            setUserInterface()
        }
        initializeGoogleMap()
        setDistance()
        checkData()
        setTimeIntervalsLongTermForecast()

        // showing progressbar according to if data is updating or not:
        viewModel.getIsUpdating().observe(this, Observer {
            showProgressBar(it)

        })
        // set weatherattributes in view when data is ready to be displayed and processed:
        viewModel.getWeather().observe(this, Observer {
            hourByHourListAdapter.notifyDataSetChanged()
            viewModel.getLongTermForecastsList(viewModel.getWeather().value!!)
            initializeLongTermForecastRecyclerView(this@LocationViewerActivity)
            // has to check for size before initializing next 24hr weather:
            if(viewModel.getWeather().value!!.weatherForecasts.size > 25){
                initializeHourlyForecastRecyclerView(this@LocationViewerActivity)
            }
        })
        //curtain-button for longterm forecast:
        location_viewer_longterm_forecast_main_container.setOnClickListener{
            if (location_viewer_longterm_forecast_container.visibility == View.GONE) {
                location_viewer_longterm_forecast_container.visibility = View.VISIBLE
                location_viewer_longterm_forecast_expand.setImageResource(R.drawable.ic_expand_less_24px)
            } else {
                location_viewer_longterm_forecast_container.visibility = View.GONE
                location_viewer_longterm_forecast_expand.setImageResource(R.drawable.ic_expand_more_24px)
            }
        }
        // functionality for favourite-icon:
        location_viewer_favorite_icon.setOnClickListener {
            editFavouriteIcon()
        }
        // Activity is reloaded upon button touch
        location_viewer_button_retry.setOnClickListener {
            recreate()
        }

        // Function lets user scroll in the map instead of scrolling the view
        mapOverlayImage.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent?) : Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        scrollView.requestDisallowInterceptTouchEvent(true)
                        return false
                    }
                    MotionEvent.ACTION_UP -> {
                        scrollView.requestDisallowInterceptTouchEvent(false)
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        scrollView.requestDisallowInterceptTouchEvent(true)
                        return false
                    }
                }
                return true
            }
        })
    }

    override fun onStop() {
        super.onStop()
        //saving updated favourites-list when activity is stopped:
        viewModel.saveFavourites()
    }
    // initializes view
    private suspend fun setUserInterface() {
        coroutineScope {
            launch {
                try {
                    viewModel.generateWeather()
                    location_viewer_container_error?.visibility = View.INVISIBLE
                }
                catch (de: DataFetchException) { //error fetching data. Show appropriate visual cues to user:
                    location_viewer_container_error?.visibility = View.VISIBLE
                    location_viewer_cardView_error?.visibility = View.VISIBLE
                    location_viewer_text_error?.text = getString(R.string.failed_weather_data_fetch_message)
                    location_viewer_container_hour_forecast.visibility = View.GONE
                    location_viewer_longterm_forecast_main_container.visibility = View.GONE
                }

            }
            launch(Main) {
                setImage()
            }
        }

    }


    // Setting distance-attribute in view. Not visible if distance is not permitted.
    private fun setDistance() {
        if (viewModel.kiteLocation.distanceToUserInKm == -1) {
            location_viewer_text_distance.visibility = View.INVISIBLE
            location_viewer_distance_icon.visibility = View.INVISIBLE
        } else {
            val distanceText = "${viewModel.kiteLocation.distanceToUserInKm} " + getString(R.string.location_distance_away)
            location_viewer_text_distance.text = distanceText
        }
    }
    
    private fun initializeGoogleMap() {
        supportMapFragment = supportFragmentManager.findFragmentById(R.id.location_viewer_container_fragment_map) as SupportMapFragment
        supportMapFragment.getMapAsync { map ->
            googleMap = map
            googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            val locationCoordinates = LatLng(viewModel.kiteLocation.latitude, viewModel.kiteLocation.longitude)
            googleMap.addMarker(MarkerOptions().position(locationCoordinates))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationCoordinates, 12F))
            googleMap.setOnMapLongClickListener{
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationCoordinates, 12F))
            }

            googleMap.setOnCameraIdleListener {
                googleMap.uiSettings.isCompassEnabled = googleMap.cameraPosition.bearing != 0F
            }

            googleMap.setOnCameraMoveListener {
                googleMap.uiSettings.isCompassEnabled = googleMap.cameraPosition.bearing != 0F
                setNorthIndicator(googleMap.cameraPosition.bearing == 0F)
            }

            setNorthIndicator(true)
        }
    }
    // setting location image from KiteLocation's image attribute:
    private fun setImage() {
        Glide.with(this)
            .load(viewModel.kiteLocation.pictureUrl)
            .into(location_viewer_picture)
    }
    // Checking if application has access to data so that recyclerviews can be initialized:
    private fun checkData() {
        try { // if weatherdata is not accessible, weather-info is not shown.
            if (viewModel.getWeather().value!!.weatherForecasts.size <= 1) {
                hourByHourListAdapter = WeatherHourByHourListAdapter(
                    mutableListOf(),
                    this,
                    viewModel)
                longTermForecastListAdapter = LongTermForecastListAdapter(viewModel.longTermForecast,
                    this,
                    viewModel)

            } else { // init recyclerviews and show data
                viewModel.getLongTermForecastsList(viewModel.getWeather().value!!)
                initializeHourlyForecastRecyclerView(this@LocationViewerActivity)
                initializeLongTermForecastRecyclerView(this@LocationViewerActivity)
            }
        } catch (e: KotlinNullPointerException) {
            Toast.makeText(baseContext, baseContext.getString(R.string.failed_weather_data_fetch_message), Toast.LENGTH_SHORT).show()
        }
    }
    // inits view for showing 24hr weather
    private fun initializeHourlyForecastRecyclerView(parentActivity: LocationViewerActivity) {
        location_viewer_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@LocationViewerActivity, LinearLayoutManager.HORIZONTAL, false)
            hourByHourListAdapter = WeatherHourByHourListAdapter(viewModel.getWeather().value!!.weatherForecasts.subList(0, 25),
                                                        parentActivity,
                                                        viewModel)
            location_viewer_recycler_view.adapter = hourByHourListAdapter
        } 
    }
    // inits view for showing long term forecast:
    private fun initializeLongTermForecastRecyclerView(parentActivity: LocationViewerActivity) {
        location_viewer_recycler_view_longterm_forecast.apply {
            layoutManager = LinearLayoutManager(this@LocationViewerActivity)
            longTermForecastListAdapter = LongTermForecastListAdapter(viewModel.longTermForecast,
                parentActivity,
                viewModel)
            location_viewer_recycler_view_longterm_forecast.adapter = longTermForecastListAdapter
        }
    }
    //makes progressbar visible/invisible according to value @param boolean
    private fun showProgressBar(boolean: Boolean) {
        if (boolean) {
            location_viewer_activity_progress_bar_hbh.visibility = View.VISIBLE
            location_viewer_container_hourly_forecast.visibility = View.INVISIBLE
        } else {
            location_viewer_activity_progress_bar_hbh.visibility = View.INVISIBLE
            location_viewer_container_hourly_forecast.visibility = View.VISIBLE
        }
    }
    // Function shows a custom compass since Google compass is invisible when it is pointing north.
    private fun setNorthIndicator(show: Boolean) {
        if (show) {
            location_viewer_map_north_indicator.visibility = View.VISIBLE
        } else {
            location_viewer_map_north_indicator.visibility = View.GONE
        }
    }
    /**
     * Show time intervals for long term forecast so that the correct weather is shown for the
     * correct time according to the timezone for the device.
     * */
    private fun setTimeIntervalsLongTermForecast() {
        val baseTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Z")).withHour(0)
        val firstIntervalStart = baseTime.withZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("kk"))
        val secondIntervalStart = baseTime.withZoneSameInstant(ZoneId.systemDefault()).plusHours(6).format(DateTimeFormatter.ofPattern("kk"))
        val thirdIntervalStart = baseTime.withZoneSameInstant(ZoneId.systemDefault()).plusHours(12).format(DateTimeFormatter.ofPattern("kk"))
        val fourthIntervalStart = baseTime.withZoneSameInstant(ZoneId.systemDefault()).plusHours(18).format(DateTimeFormatter.ofPattern("kk"))
        val firstIntervalText = "$firstIntervalStart - $secondIntervalStart"
        val secondIntervalText = "$secondIntervalStart - $thirdIntervalStart"
        val thirdIntervalText = "$thirdIntervalStart - $fourthIntervalStart"
        val fourthIntervalText = "$fourthIntervalStart - $firstIntervalStart"
        location_viewer_longterm_forecast_time_interval_one.text = firstIntervalText
        location_viewer_longterm_forecast_time_interval_two.text = secondIntervalText
        location_viewer_longterm_forecast_time_interval_three.text = thirdIntervalText
        location_viewer_longterm_forecast_time_interval_four.text = fourthIntervalText
    }

    private fun setFavouriteIcon() {
        if (viewModel.kiteLocation.isFavourite) {
           location_viewer_favorite_icon.setImageDrawable(getDrawable( R.drawable.ic_favorite_black_36dp))
        } else {
            location_viewer_favorite_icon.setImageDrawable(getDrawable( R.drawable.ic_favorite_border_black_36dp))
        }
    }

    private fun editFavouriteIcon() {
        viewModel.editFavouriteForCurrentLocation()
        setFavouriteIcon()
    }
    // when backbtn is pressed, return information about location. If favourites-status is changed, this needs to be sent back to navigation-activity:
    override fun onBackPressed() {
        val returnIntent = Intent().putExtra("location", viewModel.kiteLocation)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}
