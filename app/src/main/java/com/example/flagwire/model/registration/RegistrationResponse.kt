package com.example.flagwire.model.registration

import com.fasterxml.jackson.annotation.JsonProperty

data class RegistrationResponse(

	@field:JsonProperty("data")
	val data: List<RegistrationDataItem?>? = null,

	@field:JsonProperty("message")
	val message: String? = null,

	@field:JsonProperty("status")
	val status: Int? = null
)