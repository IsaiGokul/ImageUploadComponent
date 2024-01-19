package com.isaigokul.imageuploader

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaigokul.imageuploader.network.RetrofitClient
import com.isaigokul.imageuploader.network.data.ImageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainViewModel : ViewModel() {
    val uploadLiveData: MutableLiveData<ImageResponse> = MutableLiveData()
    val errorLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun upload(uri: Uri, selectedFile: File) {
        //image upload api call using retrofit
        viewModelScope.launch {
            val filePart= withContext(Dispatchers.IO) {
                // Step 1: Convert the file to RequestBody
                val requestBody: RequestBody = selectedFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())

                // Step 2: Create MultipartBody.Part
               MultipartBody.Part.createFormData("file", selectedFile.name, requestBody)
            }
            //image service is called with form data as input
            RetrofitClient.imageService.uploadFile(filePart).enqueue(object : Callback<ImageResponse> {
                override fun onResponse(
                    call: Call<ImageResponse>,
                    response: Response<ImageResponse>
                ) {
                    if (response.isSuccessful) {
                        // Handle success
                        Log.v("imageService upload", response.body()?.location.toString())
                        uploadLiveData.postValue(response.body())
                    } else {
                        // Handle error
                        Log.v("imageService upload", "error")
                        errorLiveData.postValue(false)
                    }
                }

                override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                    // Handle failure
                    Log.v("imageService upload", "error")
                    errorLiveData.postValue(false)
                }
            })
        }
    }



}