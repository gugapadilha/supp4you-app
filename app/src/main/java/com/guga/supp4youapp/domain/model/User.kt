package com.guga.supp4youapp.domain.model

data class User(
    val email: String,
    val password: String,
    val repeatPassword: String
)
