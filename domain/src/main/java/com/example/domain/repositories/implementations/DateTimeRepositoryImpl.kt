package com.example.domain.repositories.implementations

import com.example.domain.entities.BirthDate
import com.example.domain.isLeap
import com.example.domain.repositories.interfaces.DateTimeRepository
import java.util.*

class DateTimeRepositoryImpl : DateTimeRepository {
    override fun nextBirthday(
        birthDate: BirthDate,
        currentTime: Calendar
    ): Calendar {
        val newTime = currentTime.clone() as Calendar

        newTime[Calendar.MONTH] = birthDate.month
        newTime[Calendar.DAY_OF_MONTH] = birthDate.day
        newTime[Calendar.HOUR_OF_DAY] = 0
        newTime[Calendar.MINUTE] = 0
        newTime[Calendar.SECOND] = 0
        newTime[Calendar.MILLISECOND] = 0

        if (newTime.timeInMillis <= currentTime.timeInMillis) {
            newTime[Calendar.YEAR]++
        }

        if (birthDate.isLeap()) {
            while (!newTime.isLeap()) {
                newTime[Calendar.YEAR] =
                    (newTime[Calendar.YEAR] / 4 + 1) * 4
            }

            newTime[Calendar.MONTH] = birthDate.month
            newTime[Calendar.DAY_OF_MONTH] = birthDate.day
        }

        return newTime
    }
}