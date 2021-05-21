package com.example.domain.repositories.interfaces

import com.example.domain.entities.ContactEntity
import java.util.Calendar

interface ReminderRepository {
    fun setReminder(contactEntity: ContactEntity, toTime: Calendar)
    fun clearReminder(contactEntity: ContactEntity)

    fun hasReminder(contactEntity: ContactEntity): Boolean
}
