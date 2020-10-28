package com.example.team22.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.team22.R
import com.example.team22.activities.LocationViewerActivity
import com.example.team22.utilities.getBackgroundColorWindGusts
import com.example.team22.utilities.getBackgroundColorWindSpeed
import com.example.team22.view_models.LocationViewerViewModel
import kotlinx.android.synthetic.main.location_viewer_hourbyhour_cardview.view.*
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.roundToInt

class WeatherHourByHourListAdapter(private val weatherForecasts : MutableList<WeatherForecast>,
                                   private val parentActivity: LocationViewerActivity,
                                   private val viewModel: LocationViewerViewModel
) : RecyclerView.Adapter<WeatherHourByHourListAdapter.ViewHolder>(){


    class ViewHolder(itemView : View,
                     private val viewModel: LocationViewerViewModel
    ) : RecyclerView.ViewHolder(itemView) {
        // Fetching views from card:
        private val windSpeedText = itemView.location_cardview_wind6
        private val windGust = itemView.location_viewer_textview_hourbyhour_windgust
        private val hourOfDay = itemView.location_viewer_textview_hourbyhour_hour
        private val temperature = itemView.location_viewer_textview_hourbyhour_temperature
        private val weatherSymbol: ImageView = itemView.location_viewer_imageview_hourbyhour_weathersymbol
        private val windDirection: ImageView = itemView.location_viewer_imageview_hourbyhour_winddirection


        fun bind(weatherForecast : WeatherForecast, activity: LocationViewerActivity) {
            // Setting correct values and colors to views in card:
            windSpeedText.text = weatherForecast.windSpeed.roundToInt().toString()
            windSpeedText.setBackgroundResource(getBackgroundColorWindSpeed(weatherForecast.windSpeed, viewModel.getUserMinWindSpeed(), viewModel.getUserMaxWindSpeed()))
            windGust.text = weatherForecast.windSpeedOfGust?.roundToInt().toString()
            windGust.setBackgroundResource(getBackgroundColorWindGusts(weatherForecast.windSpeedOfGust!!, viewModel.getUserMinGust(), viewModel.getUserMaxGust()))
            hourOfDay.text = weatherForecast.dateAndTime!!.format(DateTimeFormatter.ofPattern("kk"))
            windDirection.rotation = weatherForecast.windFromDirection.toFloat()
            weatherSymbol.setImageResource(activity.resources.getIdentifier(weatherForecast.symbolCode, "drawable", activity.packageName))
            temperature.text = activity.getString(R.string.temperature_degrees, weatherForecast.temperature?.roundToInt().toString())
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(weatherForecasts[position], parentActivity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.location_viewer_hourbyhour_cardview, parent, false),
            viewModel
        )
    }

    override fun getItemCount() = weatherForecasts.size
}

