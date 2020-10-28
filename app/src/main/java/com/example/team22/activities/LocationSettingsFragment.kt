package com.example.team22.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.team22.R
import com.example.team22.view_models.UserPreferencesActivityViewModel
import io.apptik.widget.MultiSlider
import kotlinx.android.synthetic.main.fragment_location_settings.*


class LocationSettingsFragment : Fragment() {
    lateinit var viewModel : UserPreferencesActivityViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // preferences button should not be clickable, until preferences are actually changed:
        settings_button_apply_settings.isEnabled = false
        settings_button_apply_settings.backgroundTintList = ContextCompat.getColorStateList(activity as NavigationActivity, R.color.colorDisabledIndicator)
        // clickable link to redirect to applications setting, so that user can change permissions:
        val changePermissionsBtn = settings_text_permission_desc_link

        // viewmodel instance:
        viewModel =
            ViewModelProvider(this).get(UserPreferencesActivityViewModel::class.java)



        //init sliders for changing preferences:
        val windSpeedSlider = settings_activity_seekbar_windspeed
        windSpeedSlider.max = 30
        windSpeedSlider.min = 0
        val gustSpeedSlider = settings_seekbar_gustspeed
        gustSpeedSlider.max = 30
        gustSpeedSlider.min = 0

        val windSpeedOverview = settings_text_wind_speed_overview
        val gustOverview = settings_text_wind_gust_overview
        // Setting value of 'thumbs' in sliders, so they correspond with current user preferences:
        windSpeedSlider.getThumb(0).value = viewModel.windSpeedMin
        windSpeedSlider.getThumb(1).value = viewModel.windSpeedMax
        gustSpeedSlider.getThumb(0).value = viewModel.gustMin
        gustSpeedSlider.getThumb(1).value = viewModel.gustMax
        // visual cue to user showing what values sliders are currently representing:
        windSpeedOverview.text = viewModel.getWindSpeedOverviewText()
        gustOverview.text = viewModel.getGustOverviewText()

        // when user interacts with windSpeed-sliders, change user preferences according to new values:
        windSpeedSlider.setOnThumbValueChangeListener(object: MultiSlider.SimpleChangeListener() {
            override fun onValueChanged(
                multiSlider: MultiSlider,
                thumb: MultiSlider.Thumb,
                thumbIndex: Int,
                value: Int
            ) {
                //preferences are changed, user should be able to save new preferences:
                settings_button_apply_settings.isEnabled = true
                settings_button_apply_settings.backgroundTintList = ContextCompat.getColorStateList(activity as NavigationActivity, R.color.colorBlue)
                // first thumb represent low value:
                if (thumbIndex == 0) {
                    viewModel.windSpeedMin = value
                } else { // high value
                    viewModel.windSpeedMax = value
                }
                // update visual cue for user:
                windSpeedOverview.text = viewModel.getWindSpeedOverviewText()
            }
        })
        // when user interacts with windGust-sliders, change user preferences according to new values:
        gustSpeedSlider.setOnThumbValueChangeListener(object : MultiSlider.SimpleChangeListener() {
            override fun onValueChanged(
                multiSlider: MultiSlider,
                thumb: MultiSlider.Thumb,
                thumbIndex: Int,
                value: Int
            ) {
                // preferences are changed, user should be able to change preferences:
                settings_button_apply_settings.isEnabled = true
                settings_button_apply_settings.backgroundTintList = ContextCompat.getColorStateList(activity as NavigationActivity, R.color.colorBlue)
                // thumb representing low value:
                if (thumbIndex == 0) {
                    viewModel.gustMin = value
                } else { // high value:
                    viewModel.gustMax = value
                }
                gustOverview.text = viewModel.getGustOverviewText()
            }
        })
        // redirects user to application settings(android menu):
        changePermissionsBtn.setOnClickListener {
            // sets value telling program that user is currently in device application settings:
            viewModel.setUserReturnsFromSettingsCheck(true)

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context?.packageName, null)
            intent.data = uri
            startActivity(intent)

        }

        settings_button_apply_settings.setOnClickListener {
            //saving user preferences to shared preferences-file:
            viewModel.saveUserPreferences()
            // give feedback to user:
            Toast.makeText(
                activity, R.string.saved_preferences_feedback,
            Toast.LENGTH_LONG).show()
            // disable button:
            settings_button_apply_settings.isEnabled = false
            settings_button_apply_settings.backgroundTintList = ContextCompat.getColorStateList(activity as NavigationActivity, R.color.colorDisabledIndicator)

        }

        /*ONCLICK-LISTENERS FOR CURTAIN-BUTTONS:*/

        // permissions:
        settings_constraint_permissions_parent.setOnClickListener {
            if (settings_permissions_child.visibility == View.GONE) {
                settings_permissions_child.visibility = View.VISIBLE
                settings_permissions_expand.setImageResource(R.drawable.ic_expand_less_24px)
            } else {
                settings_permissions_child.visibility = View.GONE
                settings_permissions_expand.setImageResource(R.drawable.ic_expand_more_24px)
            }
        }
        // preferences:
        settings_constraint_preferences_parent.setOnClickListener {
            if (settings_constraint_preferences_child.visibility == View.GONE) {
                settings_constraint_preferences_child.visibility = View.VISIBLE
                settings_preferences_expand.setImageResource(R.drawable.ic_expand_less_24px)
            } else {
                settings_constraint_preferences_child.visibility = View.GONE
                settings_preferences_expand.setImageResource(R.drawable.ic_expand_more_24px)
            }
        }
        // about:
        settings_constraint_about_parent.setOnClickListener {
            if (settings_constraint_about_child.visibility == View.GONE) {
                settings_constraint_about_child.visibility = View.VISIBLE
                settings_about_expand.setImageResource(R.drawable.ic_expand_less_24px)
            } else {
                settings_constraint_about_child.visibility = View.GONE
                settings_about_expand.setImageResource(R.drawable.ic_expand_more_24px)
            }
        }
        // privacy:
        settings_constraint_privacy_parent.setOnClickListener {
            if (settings_constraint_privacy_child.visibility == View.GONE) {
                settings_constraint_privacy_child.visibility = View.VISIBLE
                settings_privacy_expand.setImageResource(R.drawable.ic_expand_less_24px)
            } else {
                settings_constraint_privacy_child.visibility = View.GONE
                settings_privacy_expand.setImageResource(R.drawable.ic_expand_more_24px)
            }
        }


    }

    override fun onStart() {
        super.onStart()
        // init permissions-link text according to current status of permissions:
        val permissionString: String
        if (viewModel.appHasUserLocationPermission()) {
            settings_text_switch_desc.text = getString(R.string.disallow_permissions)
            settings_location_icon.setImageDrawable(ContextCompat.getDrawable(activity as Context, R.drawable.ic_location_on_24px))
            permissionString = getString(R.string.permissions_desc_on_link)
        } else {
            settings_location_icon.setImageDrawable(ContextCompat.getDrawable(activity as Context, R.drawable.ic_location_off_24px))
            settings_text_switch_desc.text = getString(R.string.allow_permissions)
            permissionString = getString(R.string.permissions_desc_off_link)
        }
        val underlinedString = SpannableString(permissionString)
        underlinedString.setSpan(UnderlineSpan(), 0, permissionString.length, 0)
        settings_text_permission_desc_link.text = underlinedString
    }
    
}
