package com.example.flagwire.model.getdata

import com.fasterxml.jackson.annotation.JsonProperty

data class Sys(

	@field:JsonProperty("country")
	val country: String? = null,

	@field:JsonProperty("sunrise")
	val sunrise: Int? = null,

	@field:JsonProperty("sunset")
	val sunset: Int? = null,

	@field:JsonProperty("id")
	val id: Int? = null,

	@field:JsonProperty("type")
	val type: Int? = null
)