package com.example.firebaseexample1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.firebaseexample1.R
import com.example.firebaseexample1.databinding.HomeFragmentBinding

class HomeFragment:Fragment() {
    private lateinit var binding : HomeFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater)

        binding.btnFirestore.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_firestoreFragment)
        }

        binding.btnStorage.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_storageFragment)
        }

        return binding.root
    }

}