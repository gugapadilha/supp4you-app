package com.guga.supp4youapp.presentation.ui.gallery

import android.net.Uri

data class PhotoItem(val photoUri: Uri, val personName: String, val isDeleted: Boolean = false)

