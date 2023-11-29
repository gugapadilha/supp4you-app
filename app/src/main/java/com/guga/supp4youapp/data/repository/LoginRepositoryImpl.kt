package com.guga.supp4youapp.data.repository

import com.guga.supp4youapp.domain.repository.UserRepository
import com.guga.supp4youapp.domain.rest.RetrofitService
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val retrofitService: RetrofitService
) : UserRepository(retrofitService) {
}