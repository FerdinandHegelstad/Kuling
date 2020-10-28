package com.example.team22.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.team22.R
import com.example.team22.activities.LocationViewerActivity
import com.example.team22.utilities.getBackgroundColorWindSpeed
import com.example.team22.view_models.LocationViewerViewModel
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.location_viewer_longterm_cardview.view.*
import org.threeten.bp.format.TextStyle
import java.util.*
import kotlin.math.roundToInt

class LongTermForecastListAdapter(private val weatherForecasts : MutableList<MutableList<WeatherForecast>>,
                                  private val parentActivity: LocationViewerActivity,
                                  private val viewModel: LocationViewerViewModel
) : RecyclerView.Adapter<LongTermForecastListAdapter.ViewHolder>(){


    class ViewHolder(itemView : View,
                     private val viewModel: LocationViewerViewModel
    ) : RecyclerView.ViewHolder(itemView) {

        /*Fetching views from card:*/

        private val dayOfWeek: MaterialTextView = itemView.longTerm_forecast_text_day_of_week

        private val windSpeedColorOne: View = itemView.longTerm_forecast_wind_color_one
        private val windSpeedColorTwo: View  = itemView.longTerm_forecast_wind_color_two
        private val windSpeedColorThree: View  = itemView.longTerm_forecast_wind_color_three
        private val windSpeedColorFour: View  = itemView.longTerm_forecast_wind_color_four

        private val weatherSymbolOne: ImageView = itemView.longterm_forecast_symbol_one
        private val weatherSymbolTwo: ImageView = itemView.longterm_forecast_symbol_two
        private val weatherSymbolThree: ImageView = itemView.longterm_forecast_symbol_three
        private val weatherSymbolFour: ImageView = itemView.longterm_forecast_symbol_four

        private val temperatureOne: MaterialTextView = itemView.longterm_forecast_text_temperature_one
        private val temperatureTwo: MaterialTextView = itemView.longterm_forecast_text_temperature_two
        private val temperatureThree: MaterialTextView = itemView.longterm_forecast_text_temperature_three
        private val temperatureFour: MaterialTextView = itemView.longterm_forecast_text_temperature_four

        fun bind(weatherForecastList: MutableList<WeatherForecast>, activity: LocationViewerActivity) {

            dayOfWeek.text = weatherForecastList[0].dateAndTime!!.dayOfWeek.getDisplayName(TextStyle.FULL, Locale(activity.getString(R.string.language_code_iso_639)))

            /*Setting correct values for attributes in card, according to user-preferences:*/

            windSpeedColorOne.setBackgroundResource(getBackgroundColorWindSpeed(weatherForecastList[0].windSpeed, viewModel.getUserMinWindSpeed(), viewModel.getUserMaxWindSpeed()))
            windSpeedColorTwo.setBackgroundResource(getBackgroundColorWindSpeed(weatherForecastList[1].windSpeed, viewModel.getUserMinWindSpeed(), viewModel.getUserMaxWindSpeed()))
            windSpeedColorThree.setBackgroundResource(getBackgroundColorWindSpeed(weatherForecastList[2].windSpeed, viewModel.getUserMinWindSpeed(), viewModel.getUserMaxWindSpeed()))
            windSpeedColorFour.setBackgroundResource(getBackgroundColorWindSpeed(weatherForecastList[3].windSpeed, viewModel.getUserMinWindSpeed(), viewModel.getUserMaxWindSpeed()))

            weatherSymbolOne.setImageResource(activity.resources.getIdentifier(weatherForecastList[0].symbolCodeNextSixHours, "drawable", activity.packageName))
            weatherSymbolTwo.setImageResource(activity.resources.getIdentifier(weatherForecastList[1].symbolCodeNextSixHours, "drawable", activity.packageName))
            weatherSymbolThree.setImageResource(activity.resources.getIdentifier(weatherForecastList[2].symbolCodeNextSixHours, "drawable", activity.packageName))
            weatherSymbolFour.setImageResource(activity.resources.getIdentifier(weatherForecastList[3].symbolCodeNextSixHours, "drawable", activity.packageName))

            temperatureOne.text = activity.getString(R.string.temperature_degrees, weatherForecastList[0].temperature?.roundToInt().toString())
            temperatureTwo.text = activity.getString(R.string.temperature_degrees, weatherForecastList[1].temperature?.roundToInt().toString())
            temperatureThree.text = activity.getString(R.string.temperature_degrees, weatherForecastList[2].temperature?.roundToInt().toString())
            temperatureFour.text = activity.getString(R.string.temperature_degrees, weatherForecastList[3].temperature?.roundToInt().toString())
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(weatherForecasts[position], parentActivity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.location_viewer_longterm_cardview, parent, false),
            viewModel
        )
    }

    override fun getItemCount() = weatherForecasts.size
}

