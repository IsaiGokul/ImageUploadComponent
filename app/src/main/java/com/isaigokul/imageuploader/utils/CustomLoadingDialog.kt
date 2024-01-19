package com.isaigokul.imageuploader.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.ProgressBar
import com.isaigokul.imageuploader.R

class CustomLoadingDialog(context: Context) : Dialog(context) {
var progressBar:ProgressBar?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_loading_dialog)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        progressBar=findViewById(R.id.progressBar)
    }


    fun showProgressBar() {
        show()
        progressBar?.visibility = android.view.View.VISIBLE
    }

    fun hideProgressBar() {
        dismiss()
        progressBar?.visibility = android.view.View.GONE
    }
}