package com.guga.supp4youapp.domain.rest

import com.guga.supp4youapp.domain.model.LoginRequest
import com.guga.supp4youapp.domain.model.LoginResponse
import com.guga.supp4youapp.domain.model.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitService {

    @POST("register")
    fun saveUser(@Body user: User): Call<ResponseBody>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}