package com.guga.supp4youapp.presentation.ui.gallery

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.guga.supp4youapp.databinding.ActivityGalleryBinding
import com.guga.supp4youapp.presentation.ui.group.GroupManager
import com.guga.supp4youapp.presentation.ui.group.GroupModel

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

            saveGroupToSharedPreferences(groupId)

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

    private fun saveGroupToSharedPreferences(groupId: String) {
        val firestore = Firebase.firestore

        firestore.collection("create")
            .document(groupId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val groupName = document.getString("groupName")
                    val beginTime = document.getString("selectBeginTime")
                    val endTime = document.getString("selectEndTime")

                    // Salva o nome e o código do grupo nos SharedPreferences
                    val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("groupName", groupName)
                    editor.putString("groupCode", groupId)
                    editor.putString("selectBeginTime", beginTime)
                    editor.putString("selectEndTime", endTime)
                    editor.apply()

                    // Adiciona o grupo ao GroupManager se necessário
                    GroupManager.addGroup(GroupModel(groupName = groupName.toString(), groupCode = groupId!!.toInt(), beginTime = beginTime!!, endTime = endTime!!))
                    GroupManager.saveEnteredGroups(this)
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
                val photosToDelete = mutableListOf<String>() // Lista de IDs de fotos a serem excluídas

                for (document in documents) {
                    val photoUriString = document.getString("photoUri")
                    val isDeleted = document.getBoolean("isDeleted") ?: false // Obtém o status de exclusão

                    if (!photoUriString.isNullOrBlank()) {
                        val photoUri = Uri.parse(photoUriString)
                        val personName = document.getString("personName") ?: "Unknown User"

                        val photoItem = PhotoItem(
                            photoUri,
                            personName,
                            isDeleted // Define o status de exclusão no PhotoItem
                        )

                        if (isDeleted) {
                            // Se a foto estiver marcada como excluída, adicione-a à lista de fotos para exclusão
                            photosToDelete.add(document.id)
                        } else {
                            // Adicione a foto à lista de fotos a serem exibidas
                            photoItems.add(photoItem)
                        }
                    }
                }

                // Exclua as fotos marcadas para exclusão do Firestore
                for (photoId in photosToDelete) {
                    val docRef = firestore.collection("photos").document(photoId)
                    docRef.delete()
                        .addOnSuccessListener {
                            // Foto excluída com sucesso
                        }
                        .addOnFailureListener { exception ->
                            // Trate a falha na exclusão da foto
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

