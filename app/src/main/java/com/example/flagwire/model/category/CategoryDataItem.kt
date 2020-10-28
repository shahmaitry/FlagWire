package com.example.flagwire.model.category

import com.fasterxml.jackson.annotation.JsonProperty

data class CategoryDataItem(

	@field:JsonProperty("category_description")
	val categoryDescription: String? = null,

	@field:JsonProperty("category_name")
	val categoryName: String? = null,

	@field:JsonProperty("is_deleted")
	val isDeleted: String? = null,

	@field:JsonProperty("category_id")
	val categoryId: String? = null,

	@field:JsonProperty("updated_at")
	val updatedAt: String? = null,

	@field:JsonProperty("created_at")
	val createdAt: String? = null,

	var reminder_status_red: String = "false",
	var reminder_status_green: String  = "false"
)