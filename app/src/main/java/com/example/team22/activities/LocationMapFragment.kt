package com.example.team22.activities

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.team22.R
import com.example.team22.location.KiteLocation
import com.example.team22.utilities.DataFetchException
import com.example.team22.utilities.LocationCardDataClass
import com.example.team22.utilities.Next12HoursWeatherController
import com.example.team22.utilities.startNewActivity
import com.example.team22.view_models.NavigationActivityViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_location_map.*
import kotlinx.android.synthetic.main.location_map_cardview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationMapFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    val viewModel by activityViewModels<NavigationActivityViewModel>()
    private var selectedMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init activity:
        setUpGoogleMap()
        closeCard()
        setOnCardViewClick()
    }

    private fun setUpGoogleMap() {
        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.new_map) as SupportMapFragment
        // Map directs and zooms in on Norway:
        val norwayCenterCoordinates = LatLng(65.923364, 13.650883)

        mapFragment.getMapAsync { map ->
            googleMap = map
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(norwayCenterCoordinates, 5F))
            googleMap.uiSettings.isZoomControlsEnabled = true
            //adding markers for all KiteLocations on map:
            addMarkers(viewModel)
            //init clicklistener on markers. When marker is clicked, LocationCardView is visible and initialized:
            setOnClick()
        }
    }
    // Clearing map upon destruction of activity:
    override fun onDestroy() {
        super.onDestroy()
        if (this::googleMap.isInitialized) {
            googleMap.clear()
        }
    }


    private fun addMarkers(viewModel: NavigationActivityViewModel) {
        for (kiteLocation in viewModel.getLocations().value!!) {
            val coordinates = LatLng(kiteLocation.latitude, kiteLocation.longitude)
            val markerOptions = MarkerOptions()
                .title(kiteLocation.name)
                .position(coordinates)
            val marker = googleMap.addMarker(markerOptions)
            marker.tag = kiteLocation
        }
    }

    private fun setOnClick() {
        googleMap.setOnMarkerClickListener { marker ->
            resetMarker()
            selectedMarker = marker
            val cameraZoom = googleMap.cameraPosition.zoom
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedMarker!!.position, cameraZoom))

            selectedMarker!!.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            initializeCardView(marker.tag as KiteLocation)
            card_view_map.visibility = View.VISIBLE
            true
        }
    }

    private fun resetMarker() {
        selectedMarker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        selectedMarker = null
    }
    // initialize cardview for given marker on map. Sets weather and location attributes for card.
    private fun initializeCardView(kiteLocation: KiteLocation) {
        location_cardview_name.text = kiteLocation.name
        //if we are not supposed to show distance to user:
        if (kiteLocation.distanceToUserInKm == -1) {
            location_card_view_distance.text = "-"
        } else {
            val distanceText =
                "${kiteLocation.distanceToUserInKm} ${getString(R.string.kilometer_value)}"
            location_card_view_distance.visibility = View.VISIBLE
            location_card_view_distance.text = distanceText
        }
        location_card_view_text_municipality.text = kiteLocation.county

        Glide.with(this)
            .load(kiteLocation.pictureUrl)
            .centerCrop()
            .into(location_cardview_favorite)
        //Set weather-attributes:
        setUpList(kiteLocation)

    }
    // Hiding card for given marker.
    private fun closeCard() {
        location_map_cardview_close.setOnClickListener {
            card_view_map.visibility = View.GONE
            resetMarker()
        }
    }
    // if user clicks on card, user is redirected to LocationViewerActivity.
    private fun setOnCardViewClick() {
        card_view_map.setOnClickListener {
            startNewActivity(requireActivity(),
                                LocationViewerActivity::class.java,
                                selectedMarker!!.tag as KiteLocation,
                                viewModel.getFavourites().value!!)
        }
    }

    // Adding all parameters of CardView to list of LocationCardDataClasses to better keep track of attributes
    private fun setUpList(kiteLocation: KiteLocation) {
        val cardViewParameters: MutableList<LocationCardDataClass> = mutableListOf()

            cardViewParameters.add(LocationCardDataClass(location_cardview_wind1,
                location_cardview_gust1,
                location_cardview_degrees1,
                location_cardview_weathersymbol1,
                location_cardview_hour1))

            cardViewParameters.add(LocationCardDataClass(location_cardview_wind2,
                location_cardview_gust2,
                location_cardview_degrees2,
                location_cardview_weathersymbol2,
                location_cardview_hour2))

            cardViewParameters.add(LocationCardDataClass(location_cardview_wind3,
                location_cardview_gust3,
                location_cardview_degrees3,
                location_cardview_weathersymbol3,
                location_cardview_hour3))

            cardViewParameters.add(LocationCardDataClass(location_cardview_wind4,
                location_cardview_gust4,
                location_cardview_degrees4,
                location_cardview_weathersymbol4,
                location_cardview_hour4))

            cardViewParameters.add(LocationCardDataClass(location_cardview_wind5,
                location_cardview_gust5,
                location_cardview_degrees5,
                location_cardview_weathersymbol5,
                location_cardview_hour5))

            cardViewParameters.add(LocationCardDataClass(location_cardview_wind6,
                location_cardview_gust6,
                location_cardview_degrees6,
                location_cardview_weathersymbol6,
                location_cardview_hour6))

            setUpData(kiteLocation, cardViewParameters)
    }
    // Sets and displays weather data in the cardView
    private fun setUpData(location: KiteLocation, cardList: MutableList<LocationCardDataClass>) {
        location_card_view_linear_weather_info.visibility = View.GONE
        map_card_progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // sets weather if not valid:
                viewModel.setWeatherForLocation(location)
                // creating controller for calculating weather-values:
                val controller = Next12HoursWeatherController(location.weather, requireActivity())
                // for each time interval, sets correct values for card:
                for ((i, card) in cardList.withIndex()) {
                    card.windSpeedText
                        .setBackgroundResource(
                            controller
                                .getBackgroundColor2HourAverage(
                                    viewModel.getUserMinWindSpeed(),
                                    viewModel.getUserMaxWindSpeed(),
                                    viewModel.getUserMinGust(),
                                    viewModel.getUserMaxGust(), i, true
                                )
                        )

                    card.gustSpeedText
                        .setBackgroundResource(
                            controller
                                .getBackgroundColor2HourAverage(
                                    viewModel.getUserMinWindSpeed(),
                                    viewModel.getUserMaxWindSpeed(),
                                    viewModel.getUserMinGust(),
                                    viewModel.getUserMaxGust(), i, false
                                )
                        )

                    card.temperature.text = controller.get2HourInterTempAverage(i)

                    card.symbol
                        .setImageResource(
                            viewModel
                                .getApplication<Application>()
                                .resources
                                .getIdentifier(
                                    controller
                                        .getWeatherSymbolForHour(i),
                                    "drawable",
                                    viewModel.getApplication<Application>().packageName
                                )
                        )
                    card.hourText.text = controller.get2HourIntervalTimeText(i)

                    map_card_progressBar.visibility = View.GONE
                    location_card_view_linear_weather_info.visibility = View.VISIBLE
                }
            }
            catch (de : DataFetchException) {
                map_card_progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), requireContext().getString(R.string.failed_weather_data_fetch_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

}
