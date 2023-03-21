package com.guga.supp4youapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.guga.supp4youapp.domain.model.LoginRequest

class DataSourceLogin {

    companion object {

        fun createDataSetLogin2(): List<LiveData<LoginRequest>> {

            val list = ArrayList<LiveData<LoginRequest>>()
            list.add(
                MutableLiveData<LoginRequest>().apply{
                    value = LoginRequest(
                        "guga",
                        "123456"
                    )

                }


            )
            list.add(
                MutableLiveData<LoginRequest>().apply{
                    value = LoginRequest(
                        "guga.santospadilha@rede.ulbra.br",
                        "123456"
                    )

                }
            )
            return list
        }
    }

}