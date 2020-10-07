package com.example.flagwire.model.getdata

import com.fasterxml.jackson.annotation.JsonProperty

data class WeatherItem(

	@field:JsonProperty("icon")
	val icon: String? = null,

	@field:JsonProperty("description")
	val description: String? = null,

	@field:JsonProperty("main")
	val main: String? = null,

	@field:JsonProperty("id")
	val id: Int? = null
)