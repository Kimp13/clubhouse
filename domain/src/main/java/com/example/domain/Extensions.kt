package com.example.domain

import java.util.*

fun Calendar.isLeap() = get(Calendar.YEAR).let {
    it % 4 == 0 && (it % 100 != 0 || it % 400 == 0)
}