package com.example.domain

import java.util.Calendar

const val LEAP_YEAR_FREQUENCY = 4

private const val LEAP_YEAR_SKIP_FREQUENCY = 100
private const val LEAP_YEAR_SKIP_UNLESS_FREQUENCY = 400

fun Calendar.isLeap() = get(Calendar.YEAR).let {
    it % LEAP_YEAR_FREQUENCY == 0 &&
        (
            it % LEAP_YEAR_SKIP_FREQUENCY != 0 ||
                it % LEAP_YEAR_SKIP_UNLESS_FREQUENCY == 0
            )
}
