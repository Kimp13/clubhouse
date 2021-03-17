package com.example.clubhouse.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class BirthDate(
    val day: Int,
    val month: Int
) : Parcelable {
    val timeInMillis
        get() = Calendar.getInstance().apply {
            set(0, month, day)
        }.timeInMillis

    fun isLeap() = day == 29 && month == 1
}