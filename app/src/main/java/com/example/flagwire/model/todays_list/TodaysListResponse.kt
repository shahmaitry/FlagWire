package com.example.flagwire.model.todays_list

import com.fasterxml.jackson.annotation.JsonProperty

data class TodaysListResponse(

	@field:JsonProperty("data")
	val data: List<TodaysDataItem?>? = null,

	@field:JsonProperty("message")
	val message: String? = null,

	@field:JsonProperty("url")
	val url: String? = null,

	@field:JsonProperty("status")
	val status: Int? = null
)