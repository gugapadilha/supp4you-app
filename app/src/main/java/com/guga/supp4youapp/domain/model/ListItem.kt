package com.guga.supp4youapp.domain.model

sealed class ListItem(open val id: String) {
    data class DateHeader(val value: String) : ListItem(id = value)

    data class FileItem(
        override val id: String,
        val imageURL: String,
        val creatorName: String,
        val creatorUserName: String,
        val dateTimestamp: Long
    ) : ListItem(id)
}