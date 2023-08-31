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

        val firestore = Firebase.firestore
        firestore.collection("photos").get()
            .addOnSuccessListener { documents ->
                val photoItems = mutableListOf<PhotoItem>()
                for (document in documents) {
                    val photoUriString = document.getString("photoUri")
                    val photoUri = Uri.parse(photoUriString)
                    val photoItem = PhotoItem(
                        photoUri,
                        "Gustavo Padilha"
                    ) // Defina o nome do usuário apropriado
                    photoItems.add(photoItem)
                }
                // Atualize a lista de fotos no adaptador usando submitList
                galleryAdapter.submitList(photoItems)
            }
            .addOnFailureListener { exception ->
                // Lidar com falha na recuperação dos dados
            }
    }

    private fun setupRecyclerView() {
        galleryAdapter = GalleryAdapter()

        // Use LinearLayoutManager with vertical orientation
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvGallery.layoutManager = layoutManager

        binding.rvGallery.adapter = galleryAdapter

        val initialPhotoList: List<PhotoItem> = emptyList() // Lista vazia inicial
        galleryAdapter.submitList(initialPhotoList)
    }

}
