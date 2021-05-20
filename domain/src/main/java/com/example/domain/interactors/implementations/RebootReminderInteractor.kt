package com.example.domain.interactors.implementations

import com.example.domain.entities.ContactEntity
import com.example.domain.interactors.interfaces.ContactListInteractor
import com.example.domain.interactors.interfaces.ReminderInteractor
import com.example.domain.repositories.interfaces.BasicTypesRepository
import com.example.domain.repositories.interfaces.ContactRepository
import com.example.domain.repositories.interfaces.DateTimeRepository
import com.example.domain.repositories.interfaces.ReminderRepository

class RebootReminderInteractor(
    private val contactRepository: ContactRepository,
    private val reminderRepository: ReminderRepository,
    private val dateTimeRepository: DateTimeRepository,
    private val basicTypesRepository: BasicTypesRepository
) : ContactListInteractor,
    ReminderInteractor {
    override suspend fun getContacts(): List<ContactEntity> {
        return contactRepository.getContacts(
            basicTypesRepository.getAll()
                .map {
                    it.value as? String
                }
                .filterNotNull()
        )
    }

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