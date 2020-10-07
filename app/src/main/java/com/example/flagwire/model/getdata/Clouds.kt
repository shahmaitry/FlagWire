package com.example.flagwire.model.getdata

import com.fasterxml.jackson.annotation.JsonProperty

data class Clouds(

	@field:JsonProperty("all")
	val all: Int? = null
)