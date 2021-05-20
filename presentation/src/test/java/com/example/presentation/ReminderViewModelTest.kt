package com.example.presentation

import com.example.domain.entities.ContactEntity
import com.example.domain.interactors.implementations.ContactDetailsAndReminderInteractor
import com.example.presentation.ui.viewmodels.ContactDetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking

class ReminderViewModelTest {
    companion object {
        private val mainThreadSurrogate = newSingleThreadContext("UI thread")

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            Dispatchers.setMain(mainThreadSurrogate)
        }

        @AfterClass
        @JvmStatic
        fun after() {
            Dispatchers.resetMain()
            mainThreadSurrogate.close()
        }
    }

    private val contact = ContactEntity(
        id = 0,
        lookup = "Another one funny string?"
    )
    private lateinit var reminderInteractor: ContactDetailsAndReminderInteractor
    private lateinit var viewModel: ContactDetailsViewModel

    @Before
    fun before() {
        reminderInteractor = mock {
            onBlocking { getContact(any()) } doReturn contact
        }
        viewModel = ContactDetailsViewModel(reminderInteractor)
    }

    @Test
    fun setReminder() {
        verifyBlocking(reminderInteractor) {
            viewModel.setReminder(contact)?.join()

            setReminder(eq(contact))
        }
    }

    @Test
    fun clearReminder() {
        verifyBlocking(reminderInteractor) {
            viewModel.clearReminder(contact)?.join()

            clearReminder(eq(contact))
        }
    }
}
