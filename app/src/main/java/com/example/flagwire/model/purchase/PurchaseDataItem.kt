package com.example.flagwire.model.purchase

import com.fasterxml.jackson.annotation.JsonProperty

data class PurchaseDataItem(

	@field:JsonProperty("country")
	val country: String? = null,

	@field:JsonProperty("reminder")
	val reminder: String? = null,

	@field:JsonProperty("category_id")
	val categoryId: String? = null,

	@field:JsonProperty("purchase_id")
	val purchaseId: String? = null,

	@field:JsonProperty("postcode")
	val postcode: String? = null,

	@field:JsonProperty("send_via")
	val sendVia: String? = null,

	@field:JsonProperty("customer_id")
	val customerId: String? = null
)