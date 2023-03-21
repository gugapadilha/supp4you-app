package com.guga.supp4youapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.guga.supp4youapp.domain.model.User

class DataSourceRegister {

    companion object {

        fun createDataSetLogin() : List<LiveData<User>>{

            val list = ArrayList<LiveData<User>>()
            list.add(
                MutableLiveData<User>().apply {
                    value = User(
                        "guga.santospadilha@rede.ulbra.br",
                        "123456",
                        "123456"
                    )
                }
            )
            list.add(
                MutableLiveData<User>().apply {
                    value = User(
                        "guga@gmail.com",
                        "123456",
                        "123456"
                    )
                }
            )
            list.add(
                MutableLiveData<User>().apply {
                    value = User(
                        "gugapadilha@rede.ulbra.br",
                        "123456",
                        "123456"
                    )
                }
            )
            list.add(
                MutableLiveData<User>().apply {
                    value = User(
                        "gugation@gmail.com",
                        "123456",
                        "123456"
                    )
                }
            )
            list.add(
                MutableLiveData<User>().apply {
                    value = User(
                        "gugamel@gmail.com",
                        "123456",
                        "123456"
                    )
                }
            )
            return list
        }
    }

}