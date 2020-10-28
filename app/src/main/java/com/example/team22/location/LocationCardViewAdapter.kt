package com.example.team22.location

import android.app.Activity
import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.team22.R
import com.example.team22.activities.LocationViewerActivity
import com.example.team22.utilities.DataFetchException
import com.example.team22.utilities.LocationCardDataClass
import com.example.team22.utilities.Next12HoursWeatherController
import com.example.team22.utilities.startNewActivity
import com.example.team22.view_models.NavigationActivityViewModel
import kotlinx.android.synthetic.main.location_cardview.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch


class LocationCardViewAdapter(private var locationList: MutableList<KiteLocation>,

                                private val viewModel: NavigationActivityViewModel,
                                private val activity: Activity
) : RecyclerView.Adapter<LocationCardViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cardViewParameters: MutableList<LocationCardDataClass> = mutableListOf()
        private val locationName = itemView.location_cardview_name
        private val locationDistance = itemView.location_card_view_distance
        private val locationMunicipality = itemView.location_card_view_text_municipality
        private val locationImageView = itemView.location_cardview_favorite
        private val progressBar = itemView.location_card_progressBar
        private val container = itemView.location_card_view_linear_weather_info

        // adding all parameters of cardview to list of LocationCardDataClasses to better keep track of attributes:
        init {
            cardViewParameters.add(LocationCardDataClass(itemView.location_cardview_wind1,
                itemView.location_cardview_gust1,
                itemView.location_cardview_degrees1,
                itemView.location_cardview_weathersymbol1,
                itemView.location_cardview_hour1))

            cardViewParameters.add(LocationCardDataClass(itemView.location_cardview_wind2,
                itemView.location_cardview_gust2,
                itemView.location_cardview_degrees2,
                itemView.location_cardview_weathersymbol2,
                itemView.location_cardview_hour2))

            cardViewParameters.add(LocationCardDataClass(itemView.location_cardview_wind3,
                itemView.location_cardview_gust3,
                itemView.location_cardview_degrees3,
                itemView.location_cardview_weathersymbol3,
                itemView.location_cardview_hour3))

            cardViewParameters.add(LocationCardDataClass(itemView.location_cardview_wind4,
                itemView.location_cardview_gust4,
                itemView.location_cardview_degrees4,
                itemView.location_cardview_weathersymbol4,
                itemView.location_cardview_hour4))

            cardViewParameters.add(LocationCardDataClass(itemView.location_cardview_wind5,
                itemView.location_cardview_gust5,
                itemView.location_cardview_degrees5,
                itemView.location_cardview_weathersymbol5,
                itemView.location_cardview_hour5))

            cardViewParameters.add(LocationCardDataClass(itemView.location_cardview_wind6,
                itemView.location_cardview_gust6,
                itemView.location_cardview_degrees6,
                itemView.location_cardview_weathersymbol6,
                itemView.location_cardview_hour6))

        }
        fun bind (location: KiteLocation, viewModel: NavigationActivityViewModel, activity: Activity) {
            locationName.text = location.name
            container.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            // if we should not measure distance between user and location:
            if (location.distanceToUserInKm == -1) {
                locationDistance.text = "-"
            } else {
                val distanceText = "${location.distanceToUserInKm} ${activity.getString(R.string.kilometer_value)}"
                locationDistance.text = distanceText
            }
            locationMunicipality.text = location.county
            // getting user preferences:
            val userMinWindSpeed = viewModel.getUserMinWindSpeed()
            val userMaxWindSpeed = viewModel.getUserMaxWindSpeed()
            val userMinGust = viewModel.getUserMinGust()
            val userMaxGust = viewModel.getUserMaxGust()
            // Fetching weather-data for location:
            CoroutineScope(Main).launch {
                try {
                    viewModel.setWeatherForLocation(location)
                    viewModel.setWeatherDataSuccess(true)
                    // creating controller, so we can correctly calculate values for views:
                    val controller = Next12HoursWeatherController(location.weather, activity)
                    // for each time-interval (two hours each) set appropriate values using controller:
                    for ((i, card) in cardViewParameters.withIndex()) {
                        card.windSpeedText
                            .setBackgroundResource(
                                controller
                                    .getBackgroundColor2HourAverage(
                                        userMinWindSpeed,
                                        userMaxWindSpeed,
                                        userMinGust,
                                        userMaxGust, i, true
                                    )
                            )

                        card.gustSpeedText
                            .setBackgroundResource(
                                controller
                                    .getBackgroundColor2HourAverage(
                                        userMinWindSpeed,
                                        userMaxWindSpeed,
                                        userMinGust,
                                        userMaxGust, i, false
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

                    }
                    // remove progress-bar when card is presentable:
                    progressBar.visibility = View.GONE
                    container.visibility = View.VISIBLE
                }
                catch (de : DataFetchException) {
                    progressBar.visibility = View.GONE
                    viewModel.setWeatherDataSuccess(false)

                }

            }



            //set favourite-icon according to if Location is currently favourite or not:
            if (location.isFavourite) {
                itemView.location_favourites_cardview_button_favourite.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_favorite_black_36dp))
            } else {
                itemView.location_favourites_cardview_button_favourite.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_favorite_border_black_36dp))
            }

            Glide.with(itemView.context)
                .load(location.pictureUrl)
                .centerCrop()
                .into(locationImageView)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.location_cardview, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(locationList[position], viewModel, activity)

        holder.itemView.setOnClickListener{
            startNewActivity(activity, LocationViewerActivity::class.java, locationList[position], viewModel.getFavourites().value)
        }
        // onclick-listener for favourite-icon:
        holder.itemView.location_favourites_cardview_button_favourite.setOnClickListener {
            val currentLocation = locationList[position]
            if (currentLocation.isFavourite) {
                it.location_favourites_cardview_button_favourite.setImageDrawable(ContextCompat.getDrawable(it.context, R.drawable.ic_favorite_border_black_36dp))
            } else {
                it.location_favourites_cardview_button_favourite.setImageDrawable(ContextCompat.getDrawable(it.context, R.drawable.ic_favorite_black_36dp))
            }
            viewModel.editFavourite(currentLocation)
        }
    }

    override fun getItemCount() = locationList.size

    fun filterList(filteredLocations: MutableList<KiteLocation>) {
        locationList = filteredLocations
        notifyDataSetChanged()
    }

    fun setList(newList: MutableList<KiteLocation>) {
        locationList = newList
        notifyDataSetChanged()
    }
}
