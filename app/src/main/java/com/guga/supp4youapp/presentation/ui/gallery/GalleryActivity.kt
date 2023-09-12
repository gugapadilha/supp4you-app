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

        val groupId = intent.getStringExtra("groupId")

    // Recupere as fotos do Firestore
        if (groupId != null) {
            val firestore = Firebase.firestore
            firestore.collection("photos")
                .whereEqualTo("groupId", groupId)
                .get()
                .addOnSuccessListener { documents ->
                    val photoItems = mutableListOf<PhotoItem>()
                    for (document in documents) {
                        val photoUriString = document.getString("photoUri")

                        // Verifique se a URL da foto não é nula ou vazia
                        if (!photoUriString.isNullOrBlank()) {
                            val photoUri = Uri.parse(photoUriString)

                            // Obtenha o nome do usuário associado a esta foto
                            val personName = document.getString("personName") ?: "Unknown User"

                            val photoItem = PhotoItem(
                                photoUri,
                                personName // Use o nome do usuário específico para esta foto
                            )
                            photoItems.add(photoItem)
                        }
                    }
                    // Atualize a lista de fotos no adaptador usando submitList
                    galleryAdapter.submitList(photoItems)
                }
                .addOnFailureListener { exception ->
                    // Trate a falha ao recuperar as fotos do Firestore.
                }
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
