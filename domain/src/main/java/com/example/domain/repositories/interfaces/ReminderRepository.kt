package com.example.domain.repositories.interfaces

import com.example.domain.entities.ContactEntity
import java.util.*

interface ReminderRepository {
    fun setReminder(contactEntity: ContactEntity, toTime: Calendar)
    fun clearReminder(contactEntity: ContactEntity)

    fun hasReminder(contactEntity: ContactEntity): Boolean
}