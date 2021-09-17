package com.example.firebaseexample1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseexample1.dataClasses.ImageItem
import com.example.firebaseexample1.databinding.ListItemBinding
import com.squareup.picasso.Picasso

class ImagesAdapter : ListAdapter<ImageItem, ImagesAdapter.ViewHolder>(diffCallBack) {

    inner class ViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val diffCallBack = object : DiffUtil.ItemCallback<ImageItem>() {
            override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
                return oldItem.imageUri == newItem.imageUri
            }

            override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.apply {
            holder.itemView.apply {
                Picasso.get()
                    .load(item.imageUri)
                    .into(imageHolder)
            }
            delete.setOnClickListener {
                progress.visibility = View.VISIBLE
                onItemClickListener?.let {
                    it(item, position)
                }
            }
        }
    }

    //setting up click listener on recyclerview items
    private var onItemClickListener: ((ImageItem, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (ImageItem, Int) -> Unit) {
        onItemClickListener = listener
    }

}