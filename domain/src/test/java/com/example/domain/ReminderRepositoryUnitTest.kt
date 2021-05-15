package com.example.domain

import com.example.domain.entities.BirthDate
import com.example.domain.entities.ContactEntity
import com.example.domain.interactors.implementations.BirthdayNotificationInteractor
import com.example.domain.interactors.interfaces.ReminderInteractor
import com.example.domain.repositories.implementations.DateTimeRepositoryImpl
import com.example.domain.repositories.interfaces.ReminderRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argWhere
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.*

class ReminderRepositoryUnitTest {
    private val reminderRepository: ReminderRepository = mock()

    private val realDateTimeRepositoryImpl = DateTimeRepositoryImpl()

    @Test
    fun setReminderWhenBirthdayHasPassed() {
        val contact = mockContactEntity(8, Calendar.SEPTEMBER)
        val interactor = makeReminderInteractor(9, Calendar.SEPTEMBER, 1999)

        runBlocking {
            interactor.setReminder(contact)
        }

        verify(reminderRepository).setReminder(eq(contact), argWhere {
            it[Calendar.YEAR] == 2000 &&
                    it[Calendar.MONTH] == Calendar.SEPTEMBER &&
                    it[Calendar.DAY_OF_MONTH] == 8
        })
    }

    @Test
    fun setReminderWhenBirthdayHasNotPassed() {
        val contact = mockContactEntity(8, Calendar.SEPTEMBER)
        val interactor = makeReminderInteractor(7, Calendar.SEPTEMBER, 1999)

        runBlocking {
            interactor.setReminder(contact)
        }

        verify(reminderRepository).setReminder(eq(contact), argWhere {
            it[Calendar.YEAR] == 1999 &&
                    it[Calendar.MONTH] == Calendar.SEPTEMBER &&
                    it[Calendar.DAY_OF_MONTH] == 8
        })
    }

    @Test
    fun setReminderWhenNextYearIsLeap() {
        val contact = mockContactEntity(29, Calendar.FEBRUARY)
        val interactor = makeReminderInteractor(2, Calendar.MARCH, 1999)

        runBlocking {
            interactor.setReminder(contact)
        }

        verify(reminderRepository).setReminder(eq(contact), argWhere {
            it[Calendar.YEAR] == 2000 &&
                    it[Calendar.MONTH] == Calendar.FEBRUARY &&
                    it[Calendar.DAY_OF_MONTH] == 29
        })
    }

    @Test
    fun setReminderWhenNextYearIsNotLeap() {
        val contact = mockContactEntity(29, Calendar.FEBRUARY)
        val interactor = makeReminderInteractor(2, Calendar.MARCH, 2000)

        runBlocking {
            interactor.setReminder(contact)
        }

        verify(reminderRepository).setReminder(eq(contact), argWhere {
            it[Calendar.YEAR] == 2004 &&
                    it[Calendar.MONTH] == Calendar.FEBRUARY &&
                    it[Calendar.DAY_OF_MONTH] == 29
        })
    }

    @Test
    fun cancelReminder() {
        val contact = mockContactEntity(1, Calendar.JANUARY)
        val interactor = makeReminderInteractor(1, Calendar.JANUARY, 1970)

        runBlocking {
            interactor.setReminder(contact)
            interactor.clearReminder(contact)
        }

        verify(reminderRepository).clearReminder(eq(contact))
    }

    private fun makeReminderInteractor(
        day: Int,
        month: Int,
        year: Int
    ): ReminderInteractor {
        return BirthdayNotificationInteractor(
            mock(),
            reminderRepository,
            mock {
                on { nextBirthday(any(), any()) }.then {
                    realDateTimeRepositoryImpl.nextBirthday(
                        it.arguments[0] as BirthDate,
                        Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, day)
                        }
                    )
                }
            }
        )
    }

    private fun mockContactEntity(
        birthDay: Int,
        birthMonth: Int
    ): ContactEntity {
        return ContactEntity(
            id = 0,
            lookup = "What would you like me to write here?",
            birthDate = BirthDate(birthDay, birthMonth)
        )
    }
}