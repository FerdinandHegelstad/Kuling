package com.example.team22.location

import com.example.team22.utilities.makeSimpleHttpRequest
import com.example.team22.utilities.parseJSONResponseToKiteLocations
import org.junit.Test

class LocationTest {

    @Test
    fun `Assert that all values are correct`() {
        val responseBody = makeSimpleHttpRequest("https://project-7a24e.firebaseapp.com/KiteLocations.json")
        val kiteLocations = parseJSONResponseToKiteLocations(responseBody)
        kiteLocations.forEach {
            assert(it.latitude in 0.00..90.00)
            assert(it.longitude in 0.00..180.00)
        }
    }
}