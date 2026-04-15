package com.example.universityeventapp.model

import android.os.Parcel
import android.os.Parcelable

data class Event(
    val id: Int,
    val title: String,
    val date: String,
    val time: String,
    val venue: String,
    val category: String,
    val description: String,
    val price: Double,
    var totalSeats: Int,
    var availableSeats: Int,
    val imageRes: List<Int>, // List of drawable resources
    val organizer: String,
    val speakers: List<Speaker>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt(),
        mutableListOf<Int>().apply { parcel.readList(this, Int::class.java.classLoader) },
        parcel.readString().orEmpty(),
        mutableListOf<Speaker>().apply { parcel.readTypedList(this, Speaker.CREATOR) }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeString(venue)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeDouble(price)
        parcel.writeInt(totalSeats)
        parcel.writeInt(availableSeats)
        parcel.writeList(imageRes)
        parcel.writeString(organizer)
        parcel.writeTypedList(speakers)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event = Event(parcel)
        override fun newArray(size: Int): Array<Event?> = arrayOfNulls(size)
    }
}
