package com.example.universityeventapp.model

import android.os.Parcel
import android.os.Parcelable

data class Speaker(
    val name: String,
    val designation: String,
    val photoRes: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(designation)
        parcel.writeInt(photoRes)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Speaker> {
        override fun createFromParcel(parcel: Parcel): Speaker = Speaker(parcel)
        override fun newArray(size: Int): Array<Speaker?> = arrayOfNulls(size)
    }
}
