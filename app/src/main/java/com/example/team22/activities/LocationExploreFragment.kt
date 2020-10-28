package com.example.team22.activities

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team22.R
import com.example.team22.location.KiteLocation
import com.example.team22.location.LocationCardViewAdapter
import com.example.team22.utilities.TextListener
import com.example.team22.view_models.NavigationActivityViewModel
import kotlinx.android.synthetic.main.fragment_location_explore.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class LocationExploreFragment : Fragment() {
    // preparing to init listAdapter for KiteLocation-cards:
    private lateinit var listAdapter: LocationCardViewAdapter

    // using same instance of viewModel as 'parent' activity, NavigationActivity:
    val viewModel by activityViewModels<NavigationActivityViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_explore, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initializeRecyclerView(viewModel)

        // Adding textChangedListener to search field:
        locationSearchFragment_editText_searchField.addTextChangedListener(TextListener(viewModel.getLocations().value as MutableList<KiteLocation>, listAdapter, viewModel))

        // Observing changes in ViewModel's KiteLocation-data structure. Recyclerview has to dynamically change according to changes in this data structure so that UI is up to date:
        viewModel.getLocations().observe(viewLifecycleOwner, Observer {
            listAdapter.notifyDataSetChanged()
            // error-handling: If we couldn't load locations for whatever reason:
            if (viewModel.getLocations().value != null && viewModel.getLocations().value!!.isEmpty()) {
                explore_fragment_text_no_internet?.text = getString(R.string.failed_location_data_fetch_message)
                explore_fragment_button_retry?.visibility = View.VISIBLE
                explore_fragment_cardView_no_internet?.visibility = View.VISIBLE
            } else {
                explore_fragment_cardView_no_internet?.visibility = View.INVISIBLE
            }
        })

        // observing if search returns no result:
        viewModel.getEmptySearch().observe(viewLifecycleOwner, Observer {boolean ->
            if (boolean) {
                explore_fragment_text_no_result.visibility = View.VISIBLE
                val result = getString(R.string.explore_fragment_no_search_result, viewModel.getSearchString())
                explore_fragment_text_no_result.text = result
            } else {
                explore_fragment_text_no_result.visibility = View.GONE
            }
        })
        // UI-response if user is currently searching:
        viewModel.getSearchInProgress().observe(viewLifecycleOwner, Observer {boolean ->
            if (boolean) {
                explore_constraint_switch_button.visibility = View.GONE
                locationSearchFragment_clear_icon.visibility = View.VISIBLE
            } else {
                explore_constraint_switch_button.visibility = View.VISIBLE
                locationSearchFragment_clear_icon.visibility = View.INVISIBLE
            }
        })
        // updates recyclerview when distance from user to locations is finished initializing:
        viewModel.getIsLocationDistancesLoading().observe(activity as NavigationActivity, Observer { boolean ->
            if (!boolean) {
                listAdapter.notifyDataSetChanged()
            }
        })


        // Observes state of fetching data. Shows appropriate error-signaling when failing to get data from internet:
        viewModel.getDataFetchSuccess().observe(activity as NavigationActivity, Observer { boolean ->

            if (boolean) {
                explore_fragment_cardView_no_internet?.visibility = View.INVISIBLE
            } else {
                explore_fragment_button_retry?.visibility = View.INVISIBLE
                explore_fragment_cardView_no_internet?.visibility = View.VISIBLE
                explore_fragment_text_no_internet?.text = getString(R.string.failed_weather_data_fetch_message)
            }
        })
        // Cross-button in search-field. Clears search-text
        locationSearchFragment_clear_icon.setOnClickListener {
            locationSearchFragment_editText_searchField.editableText.clear()
        }
        // retry-button is visible when data-fetching failed. onClickListener tries to re-fetch missing data:
        explore_fragment_button_retry.setOnClickListener {
            CoroutineScope(Main).launch {
                explore_fragment_progress_bar.visibility = View.VISIBLE
                viewModel.generateLocations()
                viewModel.setDistanceFromUserToAllLocations()
                initializeRecyclerView(viewModel)
                explore_fragment_progress_bar.visibility = View.INVISIBLE
            }
        }
        // button that toggles how KiteLocation's are presented.
        explore_switch_button_by_proximity.setOnCheckedChangeListener { _, isChecked ->
            // if permitted, sort locations by proximity to user:
            if (isChecked) {
                if (viewModel.hasUserLocationPermission()) {
                    viewModel.sortKiteLocationsByProximity()
                } else {
                    ActivityCompat.requestPermissions(
                        activity as NavigationActivity,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1)
                    explore_switch_button_by_proximity.isChecked = false
                }
            // list alphabetically
            } else {
                viewModel.sortKiteLocationsByAlphabeticalOrder()
            }
        }
        // If soft keyboard is open - close when user scrolls.
        locationSearchFragment_recyclerView_locations.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    closeKeyBoard()
                }
            }
        })

    }

    private fun initializeRecyclerView(viewModel: NavigationActivityViewModel) {
        locationSearchFragment_recyclerView_locations.apply {
            layoutManager = LinearLayoutManager(activity)
            listAdapter = LocationCardViewAdapter(viewModel.getLocations().value as MutableList<KiteLocation>, viewModel, activity as NavigationActivity)

            adapter = listAdapter
        }
    }

    private fun closeKeyBoard() {
        val view = requireActivity().currentFocus
        view?.let {
            val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken,0)
        }
    }
    // For simplicity, when activity's onStart-method is called, KiteLocations are listed alphabetically:
    override fun onStart() {
        super.onStart()
        viewModel.sortKiteLocationsByAlphabeticalOrder()
    }
    // clearing search-field each time activity is paused(ex. changing fragment via bottom nav)
    override fun onPause() {
        super.onPause()
        locationSearchFragment_editText_searchField.editableText.clear()
    }
}
