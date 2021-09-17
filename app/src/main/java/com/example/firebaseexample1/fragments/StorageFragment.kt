package com.example.firebaseexample1.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.firebaseexample1.R
import com.example.firebaseexample1.databinding.StorageFragmentBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class StorageFragment : Fragment() {
    private lateinit var binding: StorageFragmentBinding
    private val imageRef = Firebase.storage.reference
    private var fileName: String? = ""
    private var filePath: String? = ""
    private var file: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = StorageFragmentBinding.inflate(inflater)

        binding.addButton.setOnClickListener {
            getPicture.launch("image/*")
        }

        binding.upLoadBtn.setOnClickListener {
            binding.apply {
                progressBar.isVisible = true
                progressTv.isVisible = true
            }
            uploadImage()
        }

        binding.downloadBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_storageFragment_to_imagesFragment)
        }

        return binding.root
    }

    private fun uploadImage() = CoroutineScope(Dispatchers.IO).launch {
        try {
            file?.let {
                imageRef.child("image/$fileName")
                    .putFile(it)
                    .addOnProgressListener { taskSnapShot ->
                        val progress =
                            ((100 * taskSnapShot.bytesTransferred) / taskSnapShot.totalByteCount).toInt()
                        binding.progressTv.text = ("Uploading...")
                        binding.progressBar.progress = progress
                    }
                    .await()
                withContext(Dispatchers.Main) {
                    binding.progressTv.text = ("Uploaded")
                    Toast.makeText(activity, "Image Uploaded...!", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private val getPicture = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri ->
            file = uri
            filePath = uri.path
            fileName = filePath?.substring(filePath!!.lastIndexOf('/').plus(1))
            binding.imageIv.setImageURI(uri)
        }
    }

}