package com.example.team22.utilities

import android.text.Editable
import android.text.TextWatcher
import com.example.team22.location.KiteLocation
import com.example.team22.location.LocationCardViewAdapter
import com.example.team22.view_models.NavigationActivityViewModel
import java.util.*

class TextListener(private var locations: MutableList<KiteLocation>, private var listAdapter: LocationCardViewAdapter, private  var viewModel: NavigationActivityViewModel) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        filter(s.toString())
        if (s.isNullOrEmpty()) {
            listAdapter.setList(viewModel.getLocations().value!! as MutableList<KiteLocation>)
            viewModel.setSearchInProgress(false)
        } else {
            viewModel.setSearchInProgress(true)
        }
    }

    private fun filter(text: String) {
        val filteredList = mutableListOf<KiteLocation>()
        locations.forEach {
            if (it.name.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))
                || it.county.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) {
                filteredList.add(it)
            }
        }
        viewModel.setEmptySearch(filteredList.isEmpty(), text)
        listAdapter.filterList(filteredList)
    }

}
