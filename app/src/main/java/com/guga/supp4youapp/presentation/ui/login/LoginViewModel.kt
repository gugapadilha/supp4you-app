package com.guga.supp4youapp.presentation.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.guga.supp4youapp.domain.model.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _visiblePassword = MutableLiveData(true)
    val visiblePassword: LiveData<Boolean> = _visiblePassword

    fun changeVisibilityPassowrd() {
        _visiblePassword .value = !_visiblePassword .value!!
    }

}