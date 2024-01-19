package com.isaigokul.imageuploader

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isaigokul.imageuploader.adapter.UploadAdapter
import com.isaigokul.imageuploader.utils.CustomLoadingDialog
import com.isaigokul.imageuploader.utils.ImageUploadView
import java.io.File


class MainActivity : AppCompatActivity() {
    val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    val uploadList: MutableList<String> = arrayListOf()
    lateinit var adapter: UploadAdapter
    lateinit var customLoadingDialog:CustomLoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        customLoadingDialog=CustomLoadingDialog(this)
        val imageUploadComponent: ImageUploadView = findViewById(R.id.image_upload)
        val rvImage: RecyclerView = findViewById(R.id.rv_upload)
        rvImage.layoutManager =
            GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)

        adapter = UploadAdapter(
            mutableList = uploadList,
            this,
            object : UploadAdapter.OnItemClickListener {
                override fun onItemClick(imageView: ImageView) {

                }

            })
        rvImage.adapter = adapter

        //image on submit listener

        imageUploadComponent.onSubmitListener(object : ImageUploadView.OnImageActionListener {
            override fun onSubmit(selectedFile: File, uri: Uri) {
                mainViewModel.upload(uri, selectedFile)
                imageUploadComponent.reset()
            }
        })

        //handling upload response

        mainViewModel.uploadLiveData.observe(this) { imageRespones ->
            imageRespones.location?.let {

                uploadList.add(it)
                adapter.addItem(it)
                customLoadingDialog.hideProgressBar()
                msg("uploaded successfully")
            }
        }
        mainViewModel.errorLiveData.observe(this) { state ->
            msg("Something went worng")
            customLoadingDialog.hideProgressBar()
        }
        mainViewModel.loadingLiveData.observe(this) { state ->
          if (state) customLoadingDialog.showProgressBar() else  customLoadingDialog.hideProgressBar()
        }
    }

    fun msg(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

}