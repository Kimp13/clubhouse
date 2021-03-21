package com.example.clubhouse.data

import java.util.*

fun ContactEntity.toSimple() = SimpleContactEntity(
    name,
    phoneNumbers.firstOrNull()
)

fun Calendar.isLeap() = get(Calendar.YEAR).let {
    it % 4 == 0 && (it % 100 != 0 || it % 400 == 0)
}