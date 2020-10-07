package com.example.flagwire.model.getdata
import com.fasterxml.jackson.annotation.JsonProperty

data class GetdataResponse(

	@field:JsonProperty("visibility")
	val visibility: Int? = null,

	@field:JsonProperty("timezone")
	val timezone: Int? = null,

	@field:JsonProperty("main")
	val main: Main? = null,

	@field:JsonProperty("clouds")
	val clouds: Clouds? = null,

	@field:JsonProperty("sys")
	val sys: Sys? = null,

	@field:JsonProperty("dt")
	val dt: Int? = null,

	@field:JsonProperty("coord")
	val coord: Coord? = null,

	@field:JsonProperty("weather")
	val weather: List<WeatherItem?>? = null,

	@field:JsonProperty("name")
	val name: String? = null,

	@field:JsonProperty("cod")
	val cod: Int? = null,

	@field:JsonProperty("id")
	val id: Int? = null,

	@field:JsonProperty("base")
	val base: String? = null,

	@field:JsonProperty("wind")
	val wind: Wind? = null
)