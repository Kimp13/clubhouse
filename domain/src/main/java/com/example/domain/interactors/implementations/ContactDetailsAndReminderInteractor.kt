package com.example.domain.interactors.implementations

import com.example.domain.entities.ContactEntity
import com.example.domain.interactors.interfaces.ContactDetailsInteractor
import com.example.domain.interactors.interfaces.ReminderInteractor
import com.example.domain.repositories.LocationRepository
import com.example.domain.repositories.interfaces.BasicTypesRepository
import com.example.domain.repositories.interfaces.ContactRepository
import com.example.domain.repositories.interfaces.DateTimeRepository
import com.example.domain.repositories.interfaces.ReminderRepository

open class ContactDetailsAndReminderInteractor(
    private val contactRepository: ContactRepository,
    private val reminderRepository: ReminderRepository,
    private val dateTimeRepository: DateTimeRepository,
    private val basicTypesRepository: BasicTypesRepository,
    private val locationRepository: LocationRepository
) : ContactDetailsInteractor,
    ReminderInteractor {
    override suspend fun getContact(lookup: String) =
        contactRepository.getContact(lookup)?.run {
            copy(
                location = locationRepository.findContactLocationById(id)
            )
        }

    override suspend fun setReminder(contact: ContactEntity) {
        contact.birthDate?.let {
            reminderRepository.setReminder(
                contact,
                dateTimeRepository.nextBirthday(it)
            )
        }

        basicTypesRepository.writeString(
            contact.id.toString(),
            contact.lookup
        )
    }

    override suspend fun clearReminder(contact: ContactEntity) {
        reminderRepository.clearReminder(contact)
        basicTypesRepository.remove(contact.id.toString())
    }

    override fun hasReminder(contact: ContactEntity): Boolean {
        return reminderRepository.hasReminder(contact)
    }
}