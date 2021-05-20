package com.example.domain.interactors.implementations

import com.example.domain.entities.ContactEntity
import com.example.domain.interactors.interfaces.ContactDetailsInteractor
import com.example.domain.interactors.interfaces.ReminderInteractor
import com.example.domain.repositories.interfaces.ContactRepository
import com.example.domain.repositories.interfaces.DateTimeRepository
import com.example.domain.repositories.interfaces.ReminderRepository

class BirthdayNotificationInteractor(
    private val contactRepository: ContactRepository,
    private val reminderRepository: ReminderRepository,
    private val dateTimeRepository: DateTimeRepository
) : ContactDetailsInteractor,
    ReminderInteractor {
    override suspend fun getContact(lookup: String) =
        contactRepository.getContact(lookup)

    override suspend fun setReminder(contact: ContactEntity) {
        contact.birthDate?.let {
            reminderRepository.setReminder(
                contact,
                dateTimeRepository.nextBirthday(it)
            )
        }
    }

    override suspend fun clearReminder(contact: ContactEntity) {
        reminderRepository.clearReminder(contact)
    }

    override fun hasReminder(contact: ContactEntity): Boolean {
        return reminderRepository.hasReminder(contact)
    }
}