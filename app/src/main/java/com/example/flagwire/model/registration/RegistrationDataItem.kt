package com.example.flagwire.model.registration

import com.fasterxml.jackson.annotation.JsonProperty

data class RegistrationDataItem(

	@field:JsonProperty("email_id")
	val emailId: String? = null,

	@field:JsonProperty("app_version")
	val appVersion: String? = null,

	@field:JsonProperty("created_at")
	val createdAt: String? = null,

	@field:JsonProperty("otp")
	val otp: String? = null,

	@field:JsonProperty("mobileno")
	val mobileno: String? = null,

	@field:JsonProperty("is_verified")
	val isVerified: String? = null,

	@field:JsonProperty("platform")
	val platform: String? = null,

	@field:JsonProperty("last_logged_ip")
	val lastLoggedIp: String? = null,

	@field:JsonProperty("last_logged_in")
	val lastLoggedIn: String? = null,

	@field:JsonProperty("user_id")
	val userId: String? = null,

	@field:JsonProperty("device_token")
	val deviceToken: String? = null,

	@field:JsonProperty("fullname")
	val fullname: String? = null,

	@field:JsonProperty("device_unique_id")
	val deviceUniqueId: String? = null
)