package com.example.team22.weather

import org.threeten.bp.LocalDateTime

/*Dataclass for http-GET response from locationForecast API, with default-values:*/
data class LocationForecastResponse(var statusCode : Int = -1,
                                    var responseBody : String = "",
                                    var expires : LocalDateTime = LocalDateTime.MIN)