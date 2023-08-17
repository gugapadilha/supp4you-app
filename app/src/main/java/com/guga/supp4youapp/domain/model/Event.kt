package com.guga.supp4youapp.domain.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*


data class Event(
    val key: String = "",
    val showID: String = "",
    val creator: String = "",
    val creatorUsername: String = "",
    val title: String = "",
    val thumbnail: String = "",
    val premiere: Date? = null,
    val povMaxParticipants: Int = 0,
    val maxNumberOfShots: Int = 0,
    val endDate: Double = 0.0,
    val allowContinue: AllowContinueType = AllowContinueType.After,


    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        TODO("premiere"),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(showID)
        parcel.writeString(creator)
        parcel.writeString(creatorUsername)
        parcel.writeString(title)
        parcel.writeString(thumbnail)
        parcel.writeInt(povMaxParticipants)
        parcel.writeInt(maxNumberOfShots)
        parcel.writeDouble(endDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }
    }
}

