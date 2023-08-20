package com.guga.supp4youapp.presentation.ui.gallery

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.guga.supp4youapp.R
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

        // Recupera a Uri passada como extra do Intent
        val photoUriString = intent.getStringExtra("photoUri")
        val photoUri = Uri.parse(photoUriString)

        // Crie um novo PhotoItem com a Uri da foto tirada
        val photoItem = PhotoItem(photoUri, "Gustavo Padilha")
        // Atualize a lista de fotos no adaptador usando submitList
        galleryAdapter.submitList(galleryAdapter.currentList + photoItem)
    }

    private fun setupRecyclerView() {
        galleryAdapter = GalleryAdapter()

        // Use GridLayoutManager with 2 columns
        val layoutManager = GridLayoutManager(this, 1)
        binding.rvGallery.layoutManager = layoutManager

        binding.rvGallery.adapter = galleryAdapter

        val initialPhotoList: List<PhotoItem> = emptyList() // Lista vazia inicial
        galleryAdapter.submitList(initialPhotoList)
    }

}
