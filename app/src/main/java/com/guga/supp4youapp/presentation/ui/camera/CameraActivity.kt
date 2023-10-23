package com.guga.supp4youapp.presentation.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.ActivityCameraBinding
import com.guga.supp4youapp.presentation.ui.gallery.GalleryActivity
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

class CameraActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityCameraBinding
    private var currentCameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var isFlashEnabled = false
    private lateinit var takenPhotoUri: Uri
    private lateinit var groupId: String
    private lateinit var name: String
    private lateinit var groupName: String
    private var selectedDays: String? = null
    private var selectBeginTime: String? = null
    private var selectEndTime: String? = null
    private var timeStamp: Long? = 0
    private var photoTaken = false
    private var isPhotoBeingTaken = false
    private var lastTakenPhotoUri: Uri? = null
    private var countdownTimer: CountDownTimer? = null
    private var remainingTimeMillis: Long = 0

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
        selectedDays = intent?.getStringExtra("selectDays").toString()
        selectBeginTime = intent?.getStringExtra("selectBeginTime").toString()
        selectEndTime = intent?.getStringExtra("selectEndTime").toString()
        timeStamp = intent?.getLongExtra("timestamp" , 0L)
        viewBinding.tvGroup.text = "$groupName"

        val photoUri = intent.getStringExtra("photoUri")
        if (photoUri != null) {
            val savedPhotoUri = Uri.parse(photoUri)
            // Carregue o URI da foto na ImageView (photoImageView)
            // Certifique-se de que photoImageView seja visível
            viewBinding.photoImageView.visibility = View.VISIBLE
            // Use alguma biblioteca de carregamento de imagem, como Glide ou Picasso, para carregar a imagem na ImageView
            // Exemplo com Glide:
            Glide.with(this).load(savedPhotoUri).into(viewBinding.photoImageView)
        } else {
            // Caso contrário, a ImageView deve permanecer vazia
            viewBinding.photoImageView.visibility = View.GONE
        }

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
                // Verifique se o horário atual está dentro do intervalo permitido
                if (isCurrentTimeWithinInterval(selectBeginTime!!, selectEndTime!!)) {
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
                    Toast.makeText(this, "Unavailable Time", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "You have to take a picture first before continue!", Toast.LENGTH_SHORT).show()
            }
        }
        if (savedInstanceState != null) {
            // Restaurar a URI da foto do estado anterior, se existir
            takenPhotoUri = savedInstanceState.getParcelable(SAVED_INSTANCE_STATE_URI) ?: Uri.EMPTY
            if (takenPhotoUri != Uri.EMPTY) {
                showPhoto(takenPhotoUri)
            }
        }
        updateCountdownTimer()

        viewBinding.reshot.setOnClickListener {
            if (photoTaken) {
                // Oculte a foto
                hidePhoto()

                // Defina photoTaken como falso para permitir tirar uma nova foto
                photoTaken = false

                // Exclua a foto anterior do armazenamento
                lastTakenPhotoUri?.let { uri ->
                    deletePhotoFromStorage(uri)
                    lastTakenPhotoUri = null
                }
            }
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

        updateRemainingTimeMillis()
        countdownTimer = object : CountDownTimer(remainingTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeMillis = millisUntilFinished
                updateCountdownTimer()
            }

            override fun onFinish() {
                // Este método será chamado quando o contador regressivo for concluído, se necessário
            }
        }
        countdownTimer?.start()
        updateTimestamp()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Salvar a URI da foto no estado da instância
        outState.putParcelable(SAVED_INSTANCE_STATE_URI, takenPhotoUri)
    }


    private fun takePhoto(enteredToken: String?) {
        if (isPhotoBeingTaken) {
            // Já estamos tirando uma foto, não faça nada
            return
        }

        lastTakenPhotoUri?.let { uri ->
            deletePhotoFromStorage(lastTakenPhotoUri!!)
        }

        // Defina isPhotoBeingTaken como true para evitar que outra foto seja tirada até que a atual seja salva
        isPhotoBeingTaken = true

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val photoName = UUID.randomUUID().toString()

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, photoName)
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
                        // Atualize takenPhotoUri com a URI da nova foto
                        takenPhotoUri = savedUri
                        showPhoto(takenPhotoUri)
                        photoTaken = true

                        // Atualize lastTakenPhotoUri com a URI da nova foto
                        lastTakenPhotoUri = takenPhotoUri

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



    private fun updateTimestamp() {
        // Calcule o timestamp com base na diferença entre o horário atual e selectBeginTime
        val currentDateTime = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US) // Altere o formato para "HH:mm"
        val beginTime = LocalTime.parse(selectBeginTime, formatter)
        val currentLocalTime = currentDateTime.toLocalTime()
        val secondsUntilBeginTime = Duration.between(currentLocalTime, beginTime).seconds

        // Atualize o timestamp com base no tempo restante até o início do intervalo
        timeStamp = secondsUntilBeginTime
        if (remainingTimeMillis != 0L){
            updateCountdownTimers()
            countdownTimer?.start()
        }
    }


    private fun updateCountdownTimers() {
        val seconds = (timeStamp?.rem(60))?.toInt()
        val minutes = ((timeStamp?.div(60))?.rem(60))?.toInt()
        val hours = (timeStamp?.div(3600))?.toInt()

        val formattedTime = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        viewBinding.timestamp.text = "Locked for $formattedTime"
    }

    private fun updateRemainingTimeMillis() {
        val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US) // Use "HH:mm" para um formato de 24 horas

        // Obtenha a data e hora atual no fuso horário do Brasil (Horário de Brasília)
        val currentDateTimeInBrasilia = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        val currentDateTime = LocalDateTime.ofInstant(currentDateTimeInBrasilia.toInstant(), ZoneId.of("America/Sao_Paulo"))

        val beginTime = LocalTime.parse(selectBeginTime, formatter)
        val endTime = LocalTime.parse(selectEndTime, formatter)

        // Crie um LocalDateTime para representar o início e o fim do intervalo
        val beginDateTime = currentDateTime.withHour(beginTime.hour).withMinute(beginTime.minute)
        val endDateTime = currentDateTime.withHour(endTime.hour).withMinute(endTime.minute)

        if (currentDateTime.isBefore(beginDateTime)) {
            // A hora atual está antes do início do intervalo
            // Calcule o tempo restante até o início do intervalo
            val remainingDuration = Duration.between(currentDateTime, beginDateTime)
            remainingTimeMillis = remainingDuration.toMillis()
        } else if (currentDateTime.isAfter(endDateTime)) {
            // A hora atual está após o final do intervalo, então calcule o tempo restante até o próximo dia
            val nextBeginDateTime = beginDateTime.plusDays(1)
            val remainingDuration = Duration.between(currentDateTime, nextBeginDateTime)
            remainingTimeMillis = remainingDuration.toMillis()
        } else {
            // A hora atual está dentro do intervalo permitido
            // Defina remainingTimeMillis como zero
            remainingTimeMillis = 0
        }
    }


    private fun updateCountdownTimer() {
        if (remainingTimeMillis == 0L) {
            // Está dentro do intervalo permitido, exiba a mensagem desejada
            viewBinding.timestamp.text = "You are able to take photo!"
        } else if (remainingTimeMillis > 0) {
            // Ainda não está dentro do intervalo permitido, continue mostrando o tempo restante
            val seconds = (remainingTimeMillis / 1000 % 60).toInt()
            val minutes = ((remainingTimeMillis / 1000) / 60 % 60).toInt()
            val hours = ((remainingTimeMillis / 1000) / 3600).toInt()

            val formattedTime = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
            viewBinding.timestamp.text = "Locked for $formattedTime"
        }
    }

    private fun isCurrentTimeWithinInterval(selectBeginTime: String, selectEndTime: String): Boolean {
        // Obtém a hora atual no fuso horário do Brasil (Horário de Brasília)
        val currentTime = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))

        val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US) // Altere o formato para "HH:mm"

        val beginTime = LocalTime.parse(selectBeginTime, formatter)
        val endTime = LocalTime.parse(selectEndTime, formatter)

        // Verifique se o intervalo cruza a meia-noite
        if (beginTime.isAfter(endTime)) {
            // Se o intervalo cruza a meia-noite, verifique se o horário atual é depois de beginTime
            // OU antes de endTime para determinar se está dentro do intervalo.
            return currentTime.toLocalTime().isAfter(beginTime) || currentTime.toLocalTime().isBefore(endTime)
        } else {
            // Se o intervalo não cruza a meia-noite, verifique se o horário atual está entre beginTime e endTime.
            return currentTime.toLocalTime().isAfter(beginTime) && currentTime.toLocalTime().isBefore(endTime)
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
        viewBinding.takeShotButton.visibility = View.GONE

    }

    private fun hidePhoto() {
        viewBinding.cameraPreview.visibility = View.VISIBLE
        viewBinding.photoImageView.visibility = View.GONE
        viewBinding.helpButton.visibility = View.VISIBLE
        viewBinding.flashButton.visibility = View.VISIBLE
        viewBinding.flipCameraButton.visibility = View.VISIBLE
        viewBinding.reshot.visibility = View.GONE
        viewBinding.takeShotButton.visibility = View.VISIBLE

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


    private fun deletePhotoFromStorage(uri: Uri) {
        // Extraia o caminho do URI
        val path = uri.path

        if (path != null) {
            val storage = Firebase.storage
            val storageRef = storage.reference

            // O caminho do Firebase Storage é armazenado na URI, por exemplo: "/photos/12345.jpg"
            val photoRef = storageRef.child(path)

            photoRef.delete()
                .addOnSuccessListener {
                    // A foto anterior foi excluída com sucesso
                    Log.d(TAG, "Photo deleted successfully.")

                    // Exclua a referência da foto do Firestore após a exclusão bem-sucedida do armazenamento
                    val photoUriString = uri.toString()
                    deletePhotoFromFirestore(photoUriString)
                }
                .addOnFailureListener { exception ->
                    // Trate falhas na exclusão da foto anterior aqui
                    Log.e(TAG, "Error deleting photo: $exception")
                }
        }
    }


    private fun deletePhotoFromFirestore(photoUri: String) {
        val firestore = Firebase.firestore

        // Consulte o Firestore para encontrar o documento com base na photoUri
        firestore.collection("photos")
            .whereEqualTo("photoUri", photoUri)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Exclua o documento correspondente
                    firestore.collection("photos")
                        .document(document.id)
                        .delete()
                        .addOnSuccessListener {
                            // Documento excluído com sucesso
                            Log.d(TAG, "Document deleted successfully.")
                        }
                        .addOnFailureListener { exception ->
                            // Trate a falha na exclusão do documento
                            Log.e(TAG, "Error deleting document: $exception")
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Trate a falha na consulta ao Firestore
                Log.e(TAG, "Error querying Firestore: $exception")
            }
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
        val groupId = intent.getStringExtra("groupId")

        // Consulta para definir isDeleted como true para fotos antigas do usuário
        val firestore = Firebase.firestore
        firestore.collection("photos")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("personName", name)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val oldPhotoUriString = document.getString("photoUri")
                    if (!oldPhotoUriString.isNullOrBlank()) {
                        // Define isDeleted como true para fotos antigas do usuário
                        val docRef = firestore.collection("photos").document(document.id)
                        docRef.update("isDeleted", true)
                            .addOnSuccessListener {
                                // isDeleted definido como true com sucesso
                            }
                            .addOnFailureListener { exception ->
                                // Trate a falha ao definir isDeleted como true
                            }
                    }
                }

                // Crie uma referência no Firebase Storage com um nome único para a foto
                val photoRef = storageRef.child("photos/${UUID.randomUUID()}/$photoName.jpg")

                // Realize o upload da nova foto com isDeleted como false
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
            .addOnFailureListener { exception ->
                // Trate falhas na consulta ao Firestore
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
        countdownTimer?.cancel()

    }

    companion object {
        private const val TAG = "guga"
        private const val SAVED_INSTANCE_STATE_URI = "saved_instance_state_uri"
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