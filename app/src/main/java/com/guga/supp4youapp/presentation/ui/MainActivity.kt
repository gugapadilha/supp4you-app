package com.guga.supp4youapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import checkPermission
import com.google.firebase.ktx.Firebase
import com.guga.supp4youapp.R
import com.guga.supp4youapp.presentation.ui.fragment.DetailsFragment
import showRationaleCameraDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_POV)
        setContentView(R.layout.activity_main)
    }

    private fun setupScreen() {

            val isCameraPermissionGranted = checkPermission(android.Manifest.permission.CAMERA)

            if (isCameraPermissionGranted) openCameraApp()
            else cameraRequest.launch(android.Manifest.permission.CAMERA)

    }
    private val cameraRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::handleCameraPermission
    )

    private fun handleCameraPermission(isPermitted: Boolean) {
        when {
            isPermitted -> openCameraApp()
            else -> showRationaleCameraDialog()
        }
    }

    private fun openCameraApp() {
        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Oops!")
            alert.setMessage("An error occurred. Please open your camera app and scan the QR code!")
            alert.setPositiveButton(android.R.string.ok) { d, _ ->
                d.dismiss()
            }
            alert.show()
        }
    }
}