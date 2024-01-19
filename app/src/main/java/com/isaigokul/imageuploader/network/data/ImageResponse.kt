package com.isaigokul.imageuploader.network.data

import com.google.gson.annotations.SerializedName

data class ImageResponse(

	@field:SerializedName("filename")
	val filename: String? = null,

	@field:SerializedName("originalname")
	val originalname: String? = null,

	@field:SerializedName("location")
	val location: String? = null
)
