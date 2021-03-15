package com.example.clubhouse.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.clubhouse.R
import com.example.clubhouse.data.ContactEntity
import com.example.clubhouse.databinding.FragmentContactDetailsBinding
import com.example.clubhouse.ui.interfaces.ContactServiceConsumer
import com.example.clubhouse.ui.services.ContactService
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

const val CONTACT_DETAILS_FRAGMENT_TAG = "fragment_contact_details"

private const val ARG_ID = "id"
private const val ENTITY_KEY = "contact_entity"

class ContactDetailsFragment :
    ContactServiceFragment(R.layout.fragment_contact_details),
    ContactServiceConsumer {
    companion object {
        fun newInstance(id: Int) = ContactDetailsFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_ID, id)
            }
        }
    }

    private var contact: ContactEntity? = null
    private var binding: FragmentContactDetailsBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.contact_details)
        }

        binding = FragmentContactDetailsBinding.bind(view)

        savedInstanceState?.getParcelable<ContactEntity>(ENTITY_KEY)?.let {
            updateContact(it)
        } ?: updateUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(ENTITY_KEY, contact)

        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        binding = null

        super.onDestroyView()
    }

    override fun onServiceBoundListener() {
        updateUI()
    }

    private fun updateContact(contact: ContactEntity) {
        this.contact = contact

        binding?.run {
            contactDetailsName.text = contact.name
            contactDetailsDescription.text = contact.description

            val phonesIterator = contact.phoneNumbers.listIterator()
            val emailsIterator = contact.emails.listIterator()

            listOf(
                contactDetailsPhone1,
                contactDetailsPhone2
            ).forEach {
                if (phonesIterator.hasNext()) {
                    it.text = phonesIterator.next()
                } else {
                    it.visibility = View.GONE
                }
            }

            listOf(
                contactDetailsEmail1,
                contactDetailsEmail2
            ).forEach {
                if (emailsIterator.hasNext()) {
                    it.text = emailsIterator.next()
                } else {
                    it.visibility = View.GONE
                }
            }
        }
    }

    private fun updateUI() {
        serviceOwner?.getService()?.let { service ->
            launch {
                try {
                    arguments?.getInt(ARG_ID)?.let {
                        updateContact(service.getContact(it))
                    }
                } catch (e: CancellationException) {
                    println("Interrupted")
                }
            }
        }
    }
}