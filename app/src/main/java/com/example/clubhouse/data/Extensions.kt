package com.example.clubhouse.data

fun ContactEntity.toSimple() = SimpleContactEntity(
    name,
    phoneNumbers.firstOrNull()
)