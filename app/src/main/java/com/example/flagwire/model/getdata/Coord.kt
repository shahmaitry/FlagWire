package com.example.flagwire.model.getdata

import com.fasterxml.jackson.annotation.JsonProperty

data class Coord(

	@field:JsonProperty("lon")
	val lon: Double? = null,

	@field:JsonProperty("lat")
	val lat: Double? = null
)