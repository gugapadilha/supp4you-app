package com.guga.supp4youapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.guga.supp4youapp.domain.model.LoginRequest

class DataSourceLogin {

    companion object {

        fun createDataSetLogin() : List<LiveData<LoginRequest>>{

            val list = ArrayList<LiveData<LoginRequest>>()
            list.add(
                MutableLiveData<LoginRequest>().apply {
                    value = LoginRequest(
                        "guga.santospadilha@rede.ulbra.br",
                        "123456"
                    )
                }
            )
            list.add(
                MutableLiveData<LoginRequest>().apply {
                    value = LoginRequest(
                        "guga@gmail.com",
                        "123456"
                    )
                }
            )
            list.add(
                MutableLiveData<LoginRequest>().apply {
                    value = LoginRequest(
                        "gugapadilha@rede.ulbra.br",
                        "123456"
                    )
                }
            )
            list.add(
                MutableLiveData<LoginRequest>().apply {
                    value = LoginRequest(
                        "gugation@gmail.com",
                        "123456"
                    )
                }
            )
            list.add(
                MutableLiveData<LoginRequest>().apply {
                    value = LoginRequest(
                        "gugamel@gmail.com",
                        "123456"
                    )
                }
            )
            return list
        }
    }

}