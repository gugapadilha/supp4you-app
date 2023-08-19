package com.guga.supp4youapp.presentation.ui.gallery

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
    }

    private fun setupRecyclerView() {
        galleryAdapter = GalleryAdapter()

        // Use GridLayoutManager with 2 columns
        val layoutManager = GridLayoutManager(this, 2)
        binding.rvGallery.layoutManager = layoutManager

        binding.rvGallery.adapter = galleryAdapter

        val photoList: List<PhotoItem> = createPhotoItemList()
        galleryAdapter.submitList(photoList)
    }

    private fun createPhotoItemList(): List<PhotoItem> {
        val photoList = mutableListOf<PhotoItem>()

        // Assume that the resource IDs for the photos are R.drawable.exemplo1
        val photoResId =R.drawable.exemplo1

        for (i in 1..10) {
            photoList.add(PhotoItem(photoResId, "Gustavo Padilha $i"))
        }

        return photoList
    }
}
