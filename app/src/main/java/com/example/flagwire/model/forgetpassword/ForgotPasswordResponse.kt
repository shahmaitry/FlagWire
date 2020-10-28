package com.example.flagwire.model.forgetpassword

import com.fasterxml.jackson.annotation.JsonProperty

data class ForgotPasswordResponse(

	@field:JsonProperty("message")
	val message: String? = null,

	@field:JsonProperty("status")
	val status: Int? = null
)