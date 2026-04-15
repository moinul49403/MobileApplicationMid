package com.example.ecommerceapp.model

import android.os.Parcelable
import android.os.Parcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val rating: Float,
    val category: String,
    val imageRes: Int,
    var inCart: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        name = parcel.readString().orEmpty(),
        price = parcel.readDouble(),
        rating = parcel.readFloat(),
        category = parcel.readString().orEmpty(),
        imageRes = parcel.readInt(),
        inCart = parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeFloat(rating)
        parcel.writeString(category)
        parcel.writeInt(imageRes)
        parcel.writeByte(if (inCart) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product = Product(parcel)

        override fun newArray(size: Int): Array<Product?> = arrayOfNulls(size)
    }
}
