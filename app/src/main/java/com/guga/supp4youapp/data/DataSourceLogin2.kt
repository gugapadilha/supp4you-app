package com.guga.supp4youapp.data

import com.guga.supp4youapp.domain.model.LoginRequest

class DataSourceLogin2 {

    companion object {

        fun createDataSetLogin(): ArrayList<LoginRequest> {

            val list = ArrayList<LoginRequest>()
            list.add(
                LoginRequest(
                    "guga",
                    "123456"
                )
            )
            return list
        }
    }

}