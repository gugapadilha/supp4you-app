package com.guga.supp4youapp.presentation.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.ActivityCameraBinding
import com.guga.supp4youapp.presentation.ui.gallery.GalleryActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService

class CameraActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityCameraBinding
    private var currentCameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var isFlashEnabled = false
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var takenPhotoUri: Uri
    private lateinit var groupId: String
    private lateinit var name: String
    private lateinit var groupName: String
    private var photoTaken = false
    private var isPhotoBeingTaken = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionsLauncher.launch(REQUIRED_PERMISSIONS)
        }
        groupId = intent.getStringExtra("groupId") ?: ""
        val enteredToken = intent.getStringExtra("groupId")
        name = intent.getStringExtra("personName").toString()
        groupName = intent.getStringExtra("groupName").toString()
        viewBinding.tvGroup.text = "$groupName"

        viewBinding.takeShotButton.setOnClickListener {
            if (!photoTaken) {
                takePhoto(enteredToken)
                photoTaken = true
            }
        }

            viewBinding.flipCameraButton.setOnClickListener {
            flipCamera()
        }

        viewBinding.flashButton.setOnClickListener {
            toggleFlash()
        }

        viewBinding.helpButton.setOnClickListener {
            showHelpDialog()
        }

        viewBinding.back.setOnClickListener {
            onBackPressed()
        }

        viewBinding.backIcon.setOnClickListener {
            onBackPressed()
        }

        viewBinding.continueButton.setOnClickListener {
            if (photoTaken) {
                // Defina a cor cinza para a ProgressBar
                val grayColor = ContextCompat.getColor(this, R.color.gray_200) // Substitua R.color.gray pela sua cor cinza

                // Configure a cor da ProgressBar
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewBinding.progressBar.indeterminateTintList = ColorStateList.valueOf(grayColor)
                } else {
                    val wrapDrawable = DrawableCompat.wrap(viewBinding.progressBar.indeterminateDrawable)
                    DrawableCompat.setTint(wrapDrawable, grayColor)
                    viewBinding.progressBar.indeterminateDrawable = DrawableCompat.unwrap(wrapDrawable)
                }

                viewBinding.progressBar.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, GalleryActivity::class.java)
                    intent.putExtra("groupId", groupId)
                    intent.putExtra("personName", name)
                    intent.putExtra("groupName", groupName)
                    startActivity(intent)
                    viewBinding.reshot.visibility = View.GONE
                    viewBinding.progressBar.visibility = View.GONE
                }, 1000)
            } else {
                Toast.makeText(this, "You have to take a picture first before continue!", Toast.LENGTH_SHORT).show()
            }
        }

        viewBinding.reshot.setOnClickListener {
            // Ao clicar em "reshot", oculte a foto
            hidePhoto()
            // Defina photoTaken como falso para permitir tirar uma nova foto
            photoTaken = false
        }
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionsLauncher.launch(REQUIRED_PERMISSIONS)
        }
        groupId = intent.getStringExtra("groupId") ?: ""
        name = intent.getStringExtra("personName").toString()

        // Recupere o nome do grupo usando o groupId
        fetchGroupName(groupId)

        viewBinding.takeShotButton.setOnClickListener {
            if (!photoTaken) {
                takePhoto(enteredToken)
            } else {
                // Se uma foto já foi tirada, apenas oculte a foto anterior
                hidePhoto()
            }
        }

    }

    private val pickImagesLauncher = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri>? ->
        uris?.let {
            Toast.makeText(this, "Files selected: ${uris.size}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun toggleFlash() {
        isFlashEnabled = !isFlashEnabled
        applyFlash()

        val flashIcon = if (isFlashEnabled) {
            R.drawable.ic_baseline_flash_on_24
        } else {
            R.drawable.ic_baseline_flash_off_24
        }

        viewBinding.flashButton.setImageResource(flashIcon)
    }
    private fun showPhoto(uri: Uri) {
        viewBinding.cameraPreview.visibility = View.GONE
        viewBinding.photoImageView.visibility = View.VISIBLE
        viewBinding.helpButton.visibility = View.GONE
        viewBinding.flashButton.visibility = View.GONE
        viewBinding.flipCameraButton.visibility = View.GONE
        viewBinding.photoImageView.setImageURI(takenPhotoUri)
        viewBinding.reshot.visibility = View.VISIBLE
    }

    private fun hidePhoto() {
        viewBinding.cameraPreview.visibility = View.VISIBLE
        viewBinding.photoImageView.visibility = View.GONE
        viewBinding.helpButton.visibility = View.VISIBLE
        viewBinding.flashButton.visibility = View.VISIBLE
        viewBinding.flipCameraButton.visibility = View.VISIBLE
        viewBinding.reshot.visibility = View.GONE
    }


    private fun applyFlash() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewBinding.cameraPreview.surfaceProvider)
            }

            val imageCaptureBuilder = ImageCapture.Builder()

            if (isFlashEnabled) {
                imageCaptureBuilder.setFlashMode(ImageCapture.FLASH_MODE_ON)
            } else {
                imageCaptureBuilder.setFlashMode(ImageCapture.FLASH_MODE_OFF)
            }

            imageCapture = imageCaptureBuilder.build()

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, currentCameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            finish()
        }
    }
    private fun flipCamera() {
        currentCameraSelector = if (currentCameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()
    }

    private fun takePhoto(enteredToken: String?) {
        if (isPhotoBeingTaken) {
            // Já estamos tirando uma foto, não faça nada
            return
        }

        // Defina isPhotoBeingTaken como true para evitar que outra foto seja tirada até que a atual seja salva
        isPhotoBeingTaken = true

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val photoName = UUID.randomUUID().toString()

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, photoName) // Use o nome formatado
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Supp4You")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()

        // Resto do código para tirar a foto...

        imageCapture.takePicture(outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    // Lida com o erro

                    // Defina isPhotoBeingTaken como false em caso de erro
                    isPhotoBeingTaken = false
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri
                    savedUri?.let {
                        takenPhotoUri = savedUri
                        showPhoto(takenPhotoUri)
                        photoTaken = true

                        // Chamamos o método para fazer o upload da foto para o Firebase Storage
                        uploadPhoto(takenPhotoUri)

                        val msg = "Photo capture succeeded: ${output.savedUri}"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    }

                    // Defina isPhotoBeingTaken como false após a foto ter sido salva
                    isPhotoBeingTaken = false
                }
            })
    }


    private fun showRecentPhotos() {
        pickImagesLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewBinding.cameraPreview.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()

                // Bind use cases to camera using currentCameraSelector
                cameraProvider.bindToLifecycle(
                    this, currentCameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun uploadPhoto(photoUri: Uri) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val photoName = intent.getStringExtra("personName")

        // Crie uma referência no Firebase Storage com um nome único para a foto
        val photoRef = storageRef.child("photos/${UUID.randomUUID()}/$photoName.jpg")

        // Realize o upload da foto
        val uploadTask = photoRef.putFile(photoUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // O upload da foto foi bem-sucedido, você pode obter a URL de download aqui
            photoRef.downloadUrl.addOnSuccessListener { uri ->
                // A URI da foto que você deve salvar no Firestore
                val photoUriString = uri.toString()

                // Agora você pode salvar esta URI no Firestore como fez antes
                savePhotoUriToFirestore(photoUriString)
            }
        }.addOnFailureListener { exception ->
            // Trate falhas no upload aqui
        }
    }

    private fun savePhotoUriToFirestore(photoUri: String) {
        val firestore = Firebase.firestore
        val groupId = intent.getStringExtra("groupId")
        val name = intent.getStringExtra("personName")

        val photoData = hashMapOf(
            "photoUri" to photoUri,
            "groupId" to groupId,
            "personName" to name,
            // Outros dados associados à foto, se houver
        )

        firestore.collection("photos")
            .add(photoData)
            .addOnSuccessListener { documentReference ->
                // Foto e dados salvos com sucesso no Firestore
            }
            .addOnFailureListener { exception ->
                // Trate a falha ao salvar no Firestore
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
                    viewBinding.tvGroup.text = groupName
                } else {
                    // Trate o caso em que o documento não existe
                }
            }
            .addOnFailureListener { exception ->
                // Trate a falha na consulta ao Firestore
            }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showHelpDialog() {
        val dialogMessage = "Here you can take your picture and share with other participants in this group, feel free to click Continue when you are ready! :)"

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Tips for you!")
            .setMessage(dialogMessage)
            .setPositiveButton("Continue") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val TAG = "guga"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}