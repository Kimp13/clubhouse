package com.example.domain.interactors.interfaces

import com.example.domain.entities.ContactEntity

interface ReminderInteractor {
    suspend fun setReminder(contact: ContactEntity)
    suspend fun clearReminder(contact: ContactEntity)

    fun hasReminder(contact: ContactEntity): Boolean
}
