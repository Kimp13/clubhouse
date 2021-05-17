package com.example.domain.repositories.interfaces

import com.example.domain.entities.BirthDate
import java.util.*

interface DateTimeRepository {
    fun nextBirthday(
        birthDate: BirthDate,
        currentTime: Calendar = Calendar.getInstance()
    ): Calendar
}