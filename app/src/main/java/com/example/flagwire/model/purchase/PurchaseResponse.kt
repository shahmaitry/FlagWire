package com.example.flagwire.model.purchase

import com.fasterxml.jackson.annotation.JsonProperty

data class PurchaseResponse(

    @field:JsonProperty("data")
	val data: List<PurchaseDataItem?>? = null,

    @field:JsonProperty("message")
	val message: String? = null,

    @field:JsonProperty("status")
	val status: Int? = null
)