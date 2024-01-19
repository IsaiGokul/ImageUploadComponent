package com.isaigokul.imageuploader.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.isaigokul.imageuploader.R

class UploadAdapter(
    private var mutableList: MutableList<String>,
    private var context: Activity,
    private var listener: OnItemClickListener
) : RecyclerView.Adapter<UploadAdapter.MyHolder>() {

    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById<ImageView>(R.id.iv_preview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return MyHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val item = mutableList[position]
        Glide.with(context)
            .load(item)
            .placeholder(R.drawable.ic_upload_icon)
            .into(holder.imageView);
        holder.itemView.setOnClickListener(View.OnClickListener {
            listener.onItemClick(holder.imageView)
        })
    }

    interface OnItemClickListener {
        fun onItemClick(imageView: ImageView)
    }

    override fun getItemCount(): Int {
        return mutableList.size
    }

    fun addItem(path: String) {
        if (!mutableList.contains(path)) mutableList.add(path)
        notifyDataSetChanged()
    }
}