package com.example.flagwire.model.todays_list

import com.fasterxml.jackson.annotation.JsonProperty

data class TodaysDataItem(

	@field:JsonProperty("flag_id")
	val flagId: String? = null,

	@field:JsonProperty("is_deleted")
	val isDeleted: String? = null,

	@field:JsonProperty("category_id")
	val categoryId: String? = null,

	@field:JsonProperty("flag_subtitle")
	val flagSubtitle: String? = null,

	@field:JsonProperty("flag_image")
	val flagImage: String? = null,

	@field:JsonProperty("updated_at")
	val updatedAt: String? = null,

	@field:JsonProperty("flag_hosting_date")
	val flagHostingDate: String? = null,

	@field:JsonProperty("created_at")
	val createdAt: String? = null,

	@field:JsonProperty("flag_description")
	val flagDescription: String? = null,

	@field:JsonProperty("flag_source")
	val flagSource: String? = null,

	@field:JsonProperty("flag_name")
	val flagName: String? = null
)