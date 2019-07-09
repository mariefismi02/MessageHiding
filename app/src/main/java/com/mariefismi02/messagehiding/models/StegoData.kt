package com.mariefismi02.messagehiding.models

import android.os.Parcel
import android.os.Parcelable

data class StegoData (
    var imageURI : String? = null,
    var executionTime : Long = 0,
    var sizeBefore : Int = 0,
    var sizeAfter : Int = 0,
    var message : String = "",
    var executedTime : String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageURI)
        parcel.writeLong(executionTime)
        parcel.writeInt(sizeBefore)
        parcel.writeInt(sizeAfter)
        parcel.writeString(message)
        parcel.writeString(executedTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StegoData> {
        override fun createFromParcel(parcel: Parcel): StegoData {
            return StegoData(parcel)
        }

        override fun newArray(size: Int): Array<StegoData?> {
            return arrayOfNulls(size)
        }
    }
}