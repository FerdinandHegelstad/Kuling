package com.example.team22.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.team22.R
import com.example.team22.view_models.UserPreferencesActivityViewModel
import io.apptik.widget.MultiSlider
import io.apptik.widget.MultiSlider.SimpleChangeListener
import kotlinx.android.synthetic.main.activity_user_preferences.*

class UserPreferencesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_preferences)

        val viewModel =
            ViewModelProvider(this)
                .get(UserPreferencesActivityViewModel::class.java)
        // setting min/max values for sliders:
        val windSpeedSlider = user_preferences_activity_seekbar_windspeed
        windSpeedSlider.max = 30
        windSpeedSlider.min = 0

        val gustSpeedSlider = user_preferences_activity_seekbar_gustspeed
        gustSpeedSlider.max = 30
        gustSpeedSlider.min = 0

        val windSpeedOverview = user_preferences_activity_text_wind_speed_overview
        val gustOverview = user_preferences_activity_text_wind_gust_overview
        // setting slider-thumb values according to current user preferences:
        windSpeedSlider.getThumb(0).value = viewModel.windSpeedMin
        windSpeedSlider.getThumb(1).value = viewModel.windSpeedMax
        gustSpeedSlider.getThumb(0).value = viewModel.gustMin
        gustSpeedSlider.getThumb(1).value = viewModel.gustMax
        //setting visual cues for user, showing what current thumb-values are for sliders:
        windSpeedOverview.text = viewModel.getWindSpeedOverviewText()
        gustOverview.text = viewModel.getGustOverviewText()

        /*When user interacts with windSpeed-slider, user preferences are updated, and visual cue is updated*/
        windSpeedSlider.setOnThumbValueChangeListener(object: SimpleChangeListener() {
            override fun onValueChanged(
                multiSlider: MultiSlider,
                thumb: MultiSlider.Thumb,
                thumbIndex: Int,
                value: Int
            ) {
                if (thumbIndex == 0) {
                    viewModel.windSpeedMin = value
                } else {
                    viewModel.windSpeedMax = value
                }
                windSpeedOverview.text = viewModel.getWindSpeedOverviewText()
            }
        })
        /*When user interacts with windGust-slider, user preferences are updated, and visual cue is updated*/
        gustSpeedSlider.setOnThumbValueChangeListener(object : SimpleChangeListener() {
            override fun onValueChanged(
                multiSlider: MultiSlider,
                thumb: MultiSlider.Thumb,
                thumbIndex: Int,
                value: Int
            ) {
                if (thumbIndex == 0) {
                    viewModel.gustMin = value
                } else {
                    viewModel.gustMax = value
                }
                gustOverview.text = viewModel.getGustOverviewText()
            }
        })
        // saving current/new user preferences:
        user_preferences_activity_button_apply.setOnClickListener{
            viewModel.saveUserPreferences()
            startActivity(Intent(this@UserPreferencesActivity, NavigationActivity::class.java))
        }

    }



}
