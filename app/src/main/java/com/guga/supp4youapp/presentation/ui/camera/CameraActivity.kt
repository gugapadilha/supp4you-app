package com.guga.supp4youapp.presentation.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    private lateinit var takenPhotoUri: Uri // Nova variável para armazenar a Uri da foto tirada
    private lateinit var groupId: String // Nova variável para armazenar a Uri da foto tirada
    private var photoTaken = false

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

        viewBinding.takeShotButton.setOnClickListener {
            Log.d("Debug", "groupId before takePhoto: $groupId")
            takePhoto(enteredToken) // Move isso para o clique do botão da câmera
        }

        viewBinding.flipCameraButton.setOnClickListener {
            flipCamera() // Adicione isso para o clique do botão de virar a câmera
        }

        viewBinding.flashButton.setOnClickListener {
            toggleFlash()
        }

        viewBinding.helpButton.setOnClickListener {
            showHelpDialog()
        }

        viewBinding.back.setOnClickListener {
            onBackPressed() // Volta para a tela anterior
        }

        viewBinding.backIcon.setOnClickListener {
            onBackPressed() // Volta para a tela anterior
        }



        viewBinding.continueButton.setOnClickListener {
            if (photoTaken) {
                val intent = Intent(this, GalleryActivity::class.java)
                intent.putExtra("groupId", groupId) // Passar o groupId corretamente
                startActivity(intent)
                viewBinding.reshot.visibility = View.GONE
            } else {
                Toast.makeText(this, "You have to take a picture first before continue!", Toast.LENGTH_SHORT).show()
            }
        }
        viewBinding.reshot.setOnClickListener {
            hidePhoto()
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
            R.drawable.ic_baseline_flash_on_24 // Ícone de flash ligado
        } else {
            R.drawable.ic_baseline_flash_off_24 // Ícone de flash desligado
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
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Supp4You")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri
                    savedUri?.let {
                        takenPhotoUri = savedUri
                        showPhoto(takenPhotoUri)
                        photoTaken = true // Foto tirada

                        // Adicione o código para armazenar a URI da foto no Firestore aqui
                        val firestore = Firebase.firestore
                        val photoData = hashMapOf(
                            "photoUri" to takenPhotoUri.toString(),
                            "groupId" to groupId
                        )
                        firestore.collection("photos").add(photoData)

                        val msg = "Photo capture succeeded: ${output.savedUri}"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    }
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