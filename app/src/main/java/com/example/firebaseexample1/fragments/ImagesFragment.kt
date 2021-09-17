package com.example.firebaseexample1.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.firebaseexample1.dataClasses.ImageItem
import com.example.firebaseexample1.ImagesAdapter
import com.example.firebaseexample1.databinding.ImagesFragmentBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ImagesFragment:Fragment() {
    private lateinit var binding : ImagesFragmentBinding
    private val imagesRef = Firebase.storage.reference
    private val imagesAdapter = ImagesAdapter()
    private val imagesList: ArrayList<ImageItem> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ImagesFragmentBinding.inflate(inflater)
        binding.imagesRv.adapter = imagesAdapter

        getAllImages()

        imagesAdapter.setOnItemClickListener { imageItem, position ->
            deleteImage(imageItem,position)
        }

        return binding.root
    }

    private fun getAllImages() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val images = imagesRef.child("image/").listAll().await()
            for(image in images.items) {
                val url = image.downloadUrl.await()
                imagesList.add(ImageItem(image.name,url.toString()))
            }
            withContext(Dispatchers.Main) {
                Log.e("image","$imagesList")
                imagesAdapter.submitList(imagesList)
                binding.progressBar.visibility = View.GONE
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteImage(file: ImageItem, position: Int) = CoroutineScope(Dispatchers.IO).launch {
        try {
            imagesRef.child("image/${file.imageName}").delete().await()
            withContext(Dispatchers.Main) {
                Toast.makeText(activity,"Image Deleted...!!!",Toast.LENGTH_LONG).show()
                imagesList.remove(file)
                imagesAdapter.notifyItemRemoved(position)
            }
        } catch (e:Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}