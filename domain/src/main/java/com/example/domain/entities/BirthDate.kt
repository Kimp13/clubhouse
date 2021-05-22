package com.example.domain.entities

import java.util.Calendar

private const val LEAP_YEAR_DAYS_IN_FEBRUARY = 29

data class BirthDate(
    val day: Int,
    val month: Int
) {
    val timeInMillis
        get() = Calendar.getInstance().apply {
            set(0, month, day)
        }.timeInMillis

    fun isLeap() = day == LEAP_YEAR_DAYS_IN_FEBRUARY &&
        month == Calendar.FEBRUARY
}
