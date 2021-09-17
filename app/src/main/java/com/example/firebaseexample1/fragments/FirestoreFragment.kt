package com.example.firebaseexample1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.firebaseexample1.dataClasses.Person
import com.example.firebaseexample1.databinding.FirestoreFragmentBinding
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreFragment : Fragment() {
    private lateinit var binding: FirestoreFragmentBinding
    private val personCollectionRef = Firebase.firestore.collection("persons")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FirestoreFragmentBinding.inflate(inflater)

        binding.btnUploadData.setOnClickListener {
            savePerson(getOldPerson())
        }

        binding.btnDeleteData.setOnClickListener {
            deletePerson(getOldPerson())
        }

//        binding.btnRetrieveData.setOnClickListener {
//            retrievePersons()
//        }

        subscribeToRealtimeUpdates()

        binding.btnUpdateData.setOnClickListener {
            updatePerson(getOldPerson(),getNewPerson())
        }


        return binding.root
    }

    private fun getOldPerson() : Person {
        return Person(
            binding.etFirstName.text.toString().trim(),
            binding.etLastName.text.toString().trim(),
            binding.etAge.text.toString().trim().toInt()
        )
    }

    private fun getNewPerson() : Map<String,Any> {
        val map= mutableMapOf<String,Any>()

        binding.apply {
            if(etNewFirstName.text.isNotEmpty())
                map["firstName"] = etNewFirstName.text.toString().trim()
            if(etNewLastName.text.isNotEmpty())
                map["lastName"] = etNewLastName.text.toString().trim()
            if(etNewAge.text.isNotEmpty())
                map["age"] = etNewAge.text.toString().trim().toInt()
        }

        return map
    }

    private fun updatePerson(person: Person, newPersonMap:Map<String,Any>) = CoroutineScope(
        Dispatchers.IO).launch {
        val personQuery = personCollectionRef
            .whereEqualTo("firstName",person.firstName)
            .whereEqualTo("lastName",person.lastName)
            .whereEqualTo("age",person.age)
            .get()
            .await()
        if(personQuery.documents.isNotEmpty()) {
            for(document in personQuery) {
                try {
                    personCollectionRef.document(document.id).set(
                        newPersonMap,
                        SetOptions.merge()
                    )
                } catch (e:Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(activity, "No person match found...!!!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deletePerson(person: Person) = CoroutineScope(Dispatchers.IO).launch {
        val personQuery = personCollectionRef
            .whereEqualTo("firstName",person.firstName)
            .whereEqualTo("lastName",person.lastName)
            .whereEqualTo("age",person.age)
            .get()
            .await()
        if(personQuery.documents.isNotEmpty()) {
            for(document in personQuery) {
                try {
                    personCollectionRef.document(document.id).delete().await() //to delete the whole record
//                    personCollectionRef.document(document.id).update(mapOf( //to delete a specific field from record
//                        "firstName" to FieldValue.delete()
//                    ))
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "Person deleted successfully...!!!", Toast.LENGTH_LONG).show()
                    }
                } catch (e:Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(activity, "No person match found...!!!", Toast.LENGTH_LONG).show()
            }
        }
    }

    //it will automatically update the ui as soon as data is changed in collection
    private fun subscribeToRealtimeUpdates() {
        personCollectionRef.addSnapshotListener { value, error ->
            error?.let {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                val sb = StringBuilder()
                for (document in it) {
                    val person = document.toObject<Person>()
                    sb.append("$person\n")
                }
                binding.tvPersons.text = sb.toString()
            }
        }
    }

    //for manually retrieving documents from collection
    private fun retrievePersons() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapShot = personCollectionRef
                .get()
                .await()

            val sb = StringBuilder()
            for (document in querySnapShot.documents) {
                val person = document.toObject<Person>()
                sb.append("$person\n")
            }
            withContext(Dispatchers.Main) {
                binding.tvPersons.text = sb.toString()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun savePerson(person: Person) = CoroutineScope(Dispatchers.IO).launch {
        try {
            personCollectionRef.add(person).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(activity, "Data uploaded successfully.", Toast.LENGTH_LONG)
                    .show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}