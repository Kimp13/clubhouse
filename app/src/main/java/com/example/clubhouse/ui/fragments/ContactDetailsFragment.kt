package com.example.clubhouse.ui.fragments

import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.clubhouse.R
import com.example.clubhouse.data.ContactEntity
import com.example.clubhouse.data.DataSource
import com.example.clubhouse.databinding.FragmentContactDetailsBinding
import com.example.clubhouse.ui.delegates.ReminderDelegate
import com.example.clubhouse.ui.interfaces.ContactEntityOwner
import com.example.clubhouse.ui.interfaces.ContactServiceConsumer
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

const val CONTACT_DETAILS_FRAGMENT_TAG = "fragment_contact_details"
const val CONTACT_ENTITY_KEY = "contact_entity"
const val CONTACT_ARG_LOOKUP_KEY = "argument_lookup_key"

class ContactDetailsFragment :
    ContactFragment(R.layout.fragment_contact_details),
    ContactServiceConsumer,
    ContactEntityOwner {
    companion object {
        fun newInstance(lookup: String) = ContactDetailsFragment().apply {
            arguments = Bundle().apply {
                putString(CONTACT_ARG_LOOKUP_KEY, lookup)
            }
        }
    }

    private var contactEntity: ContactEntity? = null
    private var binding: FragmentContactDetailsBinding? = null
    private var isReminded: Boolean = false
        set(value) {
            field = value

            contactEntity?.let { contact ->
                context?.let { context ->
                    if (field) {
                        ReminderDelegate.setReminder(context, contact)
                    } else {
                        ReminderDelegate.clearReminder(context, contact.id)
                    }
                }
            }
        }
        get() {
            contactEntity?.let { contact ->
                context?.let { context ->
                    return ReminderDelegate.isReminderSet(context, contact)
                }
            }

            return false
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.contact_details)
        }

        binding = FragmentContactDetailsBinding.bind(view)

        savedInstanceState?.getParcelable<ContactEntity>(CONTACT_ENTITY_KEY)?.let {
            updateContact(it)
        } ?: updateUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(CONTACT_ENTITY_KEY, contactEntity)

        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        binding = null

        super.onDestroyView()
    }

    override fun onServiceBoundListener() {
        serviceBound = true

        updateUI()
    }

    override fun getContact() = contactEntity

    private fun updateContact(contact: ContactEntity) {
        this.contactEntity = contact

        binding?.run {
            contact.photoId?.let {
                contactDetailsPhoto.run {
                    setImageURI(DataSource.makePhotoUri(it))

                    imageTintList = null
                }
            }

            contactDetailsName.text = contact.name ?: getString(R.string.no_name)
            contactDetailsDescription.text =
                contact.description ?: getString(R.string.no_description)

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

            contact.birthDate?.run {
                contactDetailsRemindTextView.visibility = View.VISIBLE

                contactDetailsRemindSwitch.run {
                    visibility = View.VISIBLE
                    isChecked = isReminded

                    setOnCheckedChangeListener { _, isChecked ->
                        isReminded = isChecked
                    }
                }

                contactDetailsBirthDate.text = getString(
                    R.string.birthday_fmt,
                    DateUtils.formatDateTime(
                        requireContext(),
                        timeInMillis,
                        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NO_YEAR
                    )
                )
            }
        }
    }

    override fun updateUI() {
        if (serviceBound && permissionGranted) {
            serviceOwner?.getService()?.let { service ->
                launch {
                    try {
                        arguments?.getString(CONTACT_ARG_LOOKUP_KEY)?.let {
                            updateContact(service.getContact(it))
                        }
                    } catch (e: CancellationException) {
                        println("Interrupted")
                    }
                }
            }
        }
    }
}