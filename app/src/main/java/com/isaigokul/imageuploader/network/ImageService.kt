package com.isaigokul.imageuploader.network

import com.isaigokul.imageuploader.network.data.ImageResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageService {
    @Multipart
    @POST("api/v1/files/upload")
    fun uploadFile(
        @Part file: MultipartBody.Part
    ): Call<ImageResponse>
}