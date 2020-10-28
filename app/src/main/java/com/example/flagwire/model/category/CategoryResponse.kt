package com.example.flagwire.model.category

import com.fasterxml.jackson.annotation.JsonProperty

data class CategoryResponse(

	@field:JsonProperty("data")
	val data: List<CategoryDataItem?>? = null,

	@field:JsonProperty("message")
	val message: String? = null,

	@field:JsonProperty("status")
	val status: Int? = null
)