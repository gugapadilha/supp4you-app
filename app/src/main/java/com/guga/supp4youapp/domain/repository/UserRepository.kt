package com.guga.supp4youapp.domain.repository

import com.guga.supp4youapp.domain.model.LoginRequest
import com.guga.supp4youapp.domain.model.User
import com.guga.supp4youapp.domain.rest.RetrofitService

open class UserRepository constructor(private val retrofitService: RetrofitService) {

    fun saveUser(user: User) = retrofitService.saveUser(user)

    fun login(loginRequest: LoginRequest) = retrofitService.login(loginRequest)
}