package com.guga.supp4youapp.presentation.ui.gallery

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.guga.supp4youapp.databinding.ActivityGalleryBinding

class GalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGalleryBinding
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.backIcon.setOnClickListener {
            onBackPressed()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            val groupId = intent.getStringExtra("groupId")
            if (groupId != null) {
                fetchGroupName(groupId)
                fetchPhotos(groupId)
            }
        }


        val groupId = intent.getStringExtra("groupId")
        if (groupId != null) {
            fetchGroupName(groupId)
            fetchPhotos(groupId)
        }
    }

    private fun fetchGroupName(groupId: String) {
        val firestore = Firebase.firestore

        firestore.collection("create")
            .document(groupId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val groupName = document.getString("groupName")
                    binding.groupCode.text = "Code: $groupId" // Atualiza o TextView com o groupId
                    binding.tvGroup.text = groupName
                } else {
                    // Trate o caso em que o documento não existe
                }
            }
            .addOnFailureListener { exception ->
                // Trate a falha na consulta ao Firestore
            }
    }


    private fun fetchPhotos(groupId: String) {
        val firestore = Firebase.firestore

        firestore.collection("photos")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { documents ->
                val photoItems = mutableListOf<PhotoItem>()
                for (document in documents) {
                    val photoUriString = document.getString("photoUri")
                    val isDeleted = document.getBoolean("isDeleted") ?: false // Obtém o status de exclusão

                    if (!photoUriString.isNullOrBlank() && !isDeleted) {
                        val photoUri = Uri.parse(photoUriString)
                        val personName = document.getString("personName") ?: "Unknown User"

                        val photoItem = PhotoItem(
                            photoUri,
                            personName,
                            isDeleted // Define o status de exclusão no PhotoItem
                        )
                        photoItems.add(photoItem)
                    }
                }
                galleryAdapter.submitList(photoItems)
                binding.swipeRefreshLayout.isRefreshing = false
            }
            .addOnFailureListener { exception ->
                // Trate a falha ao recuperar as fotos do Firestore.
                binding.swipeRefreshLayout.isRefreshing = false
            }
    }

    private fun setupRecyclerView() {
        galleryAdapter = GalleryAdapter()

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvGallery.layoutManager = layoutManager
        binding.rvGallery.adapter = galleryAdapter

        val initialPhotoList: List<PhotoItem> = emptyList()
        galleryAdapter.submitList(initialPhotoList)
    }
}
