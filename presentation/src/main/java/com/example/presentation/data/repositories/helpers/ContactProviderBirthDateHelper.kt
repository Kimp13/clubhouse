package com.example.presentation.data.repositories.helpers

import com.example.domain.entities.BirthDate

class ContactProviderBirthDateHelper {
    fun birthDateFromString(dateString: String) =
        dateString.split("-")
            .reversed()
            .let {
                BirthDate(
                    it[0].toInt(),
                    it[1].toInt()
                )
            }
}
