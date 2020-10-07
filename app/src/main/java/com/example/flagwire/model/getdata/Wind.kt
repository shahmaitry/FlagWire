package com.example.flagwire.model.getdata

import com.fasterxml.jackson.annotation.JsonProperty

data class Wind(

	@field:JsonProperty("deg")
	val deg: Int? = null,

	@field:JsonProperty("speed")
	val speed: Double? = null
)