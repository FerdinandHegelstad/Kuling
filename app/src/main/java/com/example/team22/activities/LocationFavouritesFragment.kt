package com.example.team22.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.team22.R
import com.example.team22.location.KiteLocation
import com.example.team22.location.LocationCardViewAdapter
import com.example.team22.view_models.NavigationActivityViewModel
import kotlinx.android.synthetic.main.fragment_location_favourites.*

class LocationFavouritesFragment : Fragment() {

    private lateinit var listAdapter: LocationCardViewAdapter
    private lateinit var feedback: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // same instance of viewmodel as Navigation Activity:
        val viewModel by activityViewModels<NavigationActivityViewModel>()
        // View that is visible if user has no favourites:
        feedback = locationFavouritesFragment_container_no_favourites
        initializeRecyclerView(viewModel)
        // Listen for changes in favourites-container and update recyclerView if there are changes:
        viewModel.getFavourites().observe(activity as NavigationActivity, Observer {
            listAdapter.notifyDataSetChanged()
            //showing feedback-view if favourites are empty:
            showFeedBackText(viewModel.getFavourites().value!!.isEmpty())
        })
        // updates recyclerview after location-data is finished loading:
        viewModel.getIsLocationDistancesLoading().observe(activity as NavigationActivity, Observer { boolean ->
            if (!boolean) {
                listAdapter.notifyDataSetChanged()
            }
        })
        // listening to changes in device connectivity to internet:
        viewModel.getDataFetchSuccess().observe(activity as NavigationActivity, Observer { boolean ->
            if (boolean) {
                favourites_fragment_cardView_no_internet?.visibility = View.INVISIBLE
            } else {
                favourites_fragment_cardView_no_internet?.visibility = View.VISIBLE
            }
        })
    }

    private fun showFeedBackText(boolean: Boolean) {
        if (boolean) {
            feedback.visibility = View.VISIBLE
        } else {
            feedback.visibility = View.INVISIBLE
        }
    }

    private fun initializeRecyclerView(viewModel: NavigationActivityViewModel) {
        locationFavouritesFragment_recyclerView_locations.apply {
            layoutManager = LinearLayoutManager(activity)
            listAdapter = LocationCardViewAdapter(
                viewModel.getFavourites().value!! as MutableList<KiteLocation>,
                viewModel,
                activity as NavigationActivity)

            adapter = listAdapter
        }
    }
}


