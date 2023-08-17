package com.guga.supp4youapp.presentation.ui.camera

import CountDownTimerState
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.*
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.isVisible
import checkPermissions
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.ActivityCameraBinding
import com.guga.supp4youapp.presentation.ui.login.LoginFragment
import com.guga.supp4youapp.utils.extensions.eventArgs
import com.guga.supp4youapp.utils.extensions.putEventArgs
import getAppFileDir
import getColorRes
import getDrawableRes
import openHelpChat
import setStatusBarTransparent
import showConfirmExitDialog
import showRationaleCameraDialog
import java.io.File
import java.util.*
import kotlin.properties.Delegates.observable

class CameraActivity : AppCompatActivity() {

    private val binding: ActivityCameraBinding by lazy {
        ActivityCameraBinding.inflate(layoutInflater)
    }

    private val cameraProvider: ProcessCameraProvider by lazy {
        ProcessCameraProvider.getInstance(this).get()
    }

    private val imageCapture: ImageCapture by lazy {
        ImageCapture.Builder()
            .build()
    }

    private var lensFacingDirection by observable(CameraSelector.LENS_FACING_BACK) { _, _, _ ->
        updateCameraView()
    }

    private val permissionsRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ::handlePermissions
    )

    private val screenPermissions = listOf(
        android.Manifest.permission.CAMERA
    )

    private val eventArgs by lazy { intent.eventArgs }

    private val viewModel: CameraViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        eventArgs?.let { viewModel?.setCurrentEvent(it) }
        binding.setupViews()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun ActivityCameraBinding.setupViews() {
        cameraContent.run {
            takeShotButton.setOnClickListener { view ->
                view.performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
                viewModel?.onTakeShotButtonClicked()

            }

            flipCameraButton.setOnClickListener { changeLensFacingDirection() }
            flashButton.setOnClickListener { changeFlashMode() }
            // TODO: When we create event screen, we should un-comment this section
            //eventThumbImage.setOnClickListener { onBackPressed() }
            backButton.setOnClickListener { onBackPressed() }
            helpButton.setOnClickListener { openHelpChat() }
        }
    }
    override fun onStart() {
        setupCamera()
        super.onStart()
    }

    override fun onBackPressed() {
            val intent = eventArgs?.let {
                Intent(this, LoginFragment::class.java)
                    .putEventArgs(it)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
            finish()
    }

    private fun showTakePhotoDisabledToast(isGalleryClosed: Boolean) {
        val textRes =
            if (isGalleryClosed) R.string.photo_disabled_toast_gallery_closed
            else R.string.photo_disabled_toast_event_ended

        Toast.makeText(this, textRes, Toast.LENGTH_SHORT).show()
    }

    private fun setupCamera() {
        val hasPermissions = checkPermissions(screenPermissions)

        if (hasPermissions) updateCameraView()
        else permissionsRequest.launch(screenPermissions.toTypedArray())
    }

    private fun PreviewView.bind(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacingDirection)
            .build()

        preview.setSurfaceProvider(surfaceProvider)

        cameraProvider.bindToLifecycle(
            this@CameraActivity,
            cameraSelector,
            imageCapture,
            preview
        )
    }

    private fun updateCameraView() {
        cameraProvider.unbindAll()
        binding.cameraPreview.bind(cameraProvider)
    }

    private fun handlePermissions(permissions: Map<String, Boolean>) {
        val arePermissionsGranted = permissions.values.all { isGranted -> isGranted }

        if (arePermissionsGranted) updateCameraView()
        else showRationaleCameraDialog()
    }

    private fun takeShot() {
        val cameraMainExecutor = ContextCompat.getMainExecutor(this)
        val fileName = "${Date().time}.jpg"
        val shotFile = File(getAppFileDir(), fileName)

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(shotFile)
            .build()

        imageCapture.takePicture(
            outputOptions,
            cameraMainExecutor,
            onImageSavedCallback
        )
    }

    private fun changeLensFacingDirection() {
        lensFacingDirection = if (lensFacingDirection == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
    }

    private fun changeFlashMode() = with(binding.cameraContent) {
        val isFlashOn = imageCapture.flashMode == FLASH_MODE_ON
        imageCapture.flashMode = if (isFlashOn) {
            flashButton.setImageResource(R.drawable.ic_baseline_flash_off_24)
            FLASH_MODE_OFF
        } else {
            flashButton.setImageResource(R.drawable.ic_baseline_flash_on_24)
            FLASH_MODE_ON
        }
    }

    private val onImageSavedCallback = object : OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val savedFile = outputFileResults.savedUri?.toFile()?.also {
                viewModel?.uploadFile(it)
            }
        }

        override fun onError(exception: ImageCaptureException) {
         //error
        }
    }

    private fun shareEventLink() {
        val appLink = "https://pov.camera"
        val sharedMessage = "I'm using POV at '${eventArgs?.title}'! $appLink"
        ShareCompat.IntentBuilder(this)
            .setType("text/plain")
            .setText(sharedMessage)
            .startChooser()
    }

    private fun setCountDownTimerState(state: CountDownTimerState) {
        with(binding) {
            countdownLabel.text = if (state.type == CountDownTimerType.END_DATE) {
                getString(R.string.camera_closes_in_label)
            } else {
                getString(R.string.gallery_opens_in_label)
            }
            countdownContainer.isVisible =
                state.type != CountDownTimerType.DONE && state.type != CountDownTimerType.INITIAL
        }
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    private fun setContinueButtonState(isEnabled: Boolean) {
        with(binding.continueButton) {
            if (isEnabled) {
                background = getDrawableRes(R.drawable.background_rounded_button)
                setTextColor(getColorRes(R.color.white))
                compoundDrawableTintList = ColorStateList.valueOf(getColorRes(R.color.white))
//                setOnClickListener { navigateToGallery() }
            } else {
                background = getDrawableRes(R.drawable.background_rounded_button_disabled)
                setTextColor(getColorRes(R.color.purple_200))
                compoundDrawableTintList = ColorStateList.valueOf(getColorRes(R.color.purple_200))
//                setOnClickListener { shake(this@CameraActivity) }
            }
        }
    }

//    private fun navigateToGallery() {
//        val intent = Intent(
//            this,
//            GalleryActivity::class.java
//        ).putEventArgs(eventArgs)
//
//        startActivity(intent)
//    }

    private fun setShotButtonState(isEnabled: Boolean) {
        with(binding.cameraContent.takeShotButton) {
            if (isEnabled) {
                background = getDrawableRes(R.drawable.background_shot_button)
                setImageResource(0) // remove icon
            } else {
                background = getDrawableRes(R.drawable.background_shot_button_disabled)
                setImageResource(R.drawable.ic_lock)
            }
        }
    }
}