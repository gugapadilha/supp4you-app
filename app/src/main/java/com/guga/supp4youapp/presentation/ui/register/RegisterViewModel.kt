package com.guga.supp4youapp.presentation.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.guga.supp4youapp.data.DataSourceRegister
import com.guga.supp4youapp.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {

    private val _data = MutableLiveData<List<User>>()
    val data: MutableLiveData<List<User>> = _data


    fun validateRegister(username: String, password: String, rePassword: String): Boolean {
        if (username.isEmpty() || password.isEmpty()) {
            return false
        }

        if (!username.contains("@")) {
            return false
        }

        if (password.isEmpty() && rePassword != password){
            return false
        }

        val loginDataSet = DataSourceRegister.createDataSetLogin()

        for (loginRequest in loginDataSet) {
            if (loginRequest.value?.email == username && loginRequest.value?.password == password && loginRequest.value?.repeatPassword == rePassword) {
                return true
            }
        }
        return false
    }
}