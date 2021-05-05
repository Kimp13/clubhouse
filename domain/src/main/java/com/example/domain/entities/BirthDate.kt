package com.example.domain.entities

import java.util.*

data class BirthDate(
    val day: Int,
    val month: Int
) {
    val timeInMillis
        get() = Calendar.getInstance().apply {
            set(0, month, day)
        }.timeInMillis

    fun isLeap() = day == 29 && month == 1
}