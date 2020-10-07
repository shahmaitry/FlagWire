package com.example.flagwire.model.getdata

import com.fasterxml.jackson.annotation.JsonProperty

data class Main(

	@field:JsonProperty("temp")
	val temp: Double? = null,

	@field:JsonProperty("temp_min")
	val tempMin: Double? = null,

	@field:JsonProperty("humidity")
	val humidity: Int? = null,

	@field:JsonProperty("pressure")
	val pressure: Int? = null,

	@field:JsonProperty("feels_like")
	val feelsLike: Double? = null,

	@field:JsonProperty("temp_max")
	val tempMax: Double? = null
)