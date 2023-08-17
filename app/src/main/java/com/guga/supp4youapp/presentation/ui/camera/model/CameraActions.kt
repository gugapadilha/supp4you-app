package com.guga.supp4youapp.presentation.ui.camera.model

import androidx.annotation.StringRes

sealed interface CameraActions {
    object TakeShot : CameraActions
    data class ShowShotsDisabledToast(val isGalleryClosed: Boolean) : CameraActions
}