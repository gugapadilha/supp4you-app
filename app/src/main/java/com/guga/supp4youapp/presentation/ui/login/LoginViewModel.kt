package com.guga.supp4youapp.presentation.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.guga.supp4youapp.data.DataSourceLogin
import com.guga.supp4youapp.domain.model.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _data = MutableLiveData<List<LoginRequest>>()
    val data: MutableLiveData<List<LoginRequest>> = _data

    fun validateLogin(username: String, password: String): Boolean {
        val loginDataSet = DataSourceLogin.createDataSetLogin()

        for (loginRequest in loginDataSet) {
            if (loginRequest.value?.email == username && loginRequest.value?.password == password) {
                return true
            }
        }
        return false
    }

}