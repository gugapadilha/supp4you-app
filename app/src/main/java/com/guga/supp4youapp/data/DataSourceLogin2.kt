package com.guga.supp4youapp.data

import com.guga.supp4youapp.domain.model.LoginRequest

class DataSourceLogin2 {

    companion object {

        fun createDataSetLogin2(): ArrayList<LoginRequest> {

            val list = ArrayList<LoginRequest>()
            list.add(
                LoginRequest(
                    "guga",
                    "123456"
                )
            )
            list.add(
                LoginRequest(
                    "guga.santospadilha@rede.ulbra.br",
                    "123456"
                )
            )
            return list
        }
    }

}