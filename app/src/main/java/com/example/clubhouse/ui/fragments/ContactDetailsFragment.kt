package com.example.clubhouse.ui.fragments

import android.os.Bundle
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.viewModels
import com.example.clubhouse.R
import com.example.clubhouse.data.entities.ContactEntity
import com.example.clubhouse.databinding.FragmentContactDetailsBinding
import com.example.clubhouse.ui.delegates.ContactPhotoDelegate
import com.example.clubhouse.ui.delegates.ReminderDelegate
import com.example.clubhouse.ui.interfaces.ReadContactsPermissionRequester
import com.example.clubhouse.ui.viewmodels.ContactDetailsViewModel

const val CONTACT_DETAILS_FRAGMENT_TAG = "fragment_contact_details"
const val CONTACT_ARG_LOOKUP_KEY = "argument_lookup_key"

class ContactDetailsFragment :
    ContactFragment(R.layout.fragment_contact_details) {
    companion object {
        fun newInstance(lookup: String) = ContactDetailsFragment().apply {
            arguments = Bundle().apply {
                putString(CONTACT_ARG_LOOKUP_KEY, lookup)
            }
        }
    }

    private val viewModel: ContactDetailsViewModel by viewModels {
        viewModelFactory
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

        initializeRefreshView()
        setHasOptionsMenu(true)
        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.fragment_contact_details_menu, menu)
        menu.findItem(R.id.menuRefresh).setOnMenuItemClickListener {
            binding?.contactDetailsRefreshView?.isRefreshing = true
            refreshContactDetails()

            true
        }
    }

    override fun onDestroyView() {
        binding = null

        super.onDestroyView()
    }

    private fun updateContact(contact: ContactEntity) {
        this.contactEntity = contact

        binding?.run {
            if (!contactDetailsRefreshView.isEnabled) {
                contactDetailsRefreshView.isEnabled = true
                contactDetailsContents.removeView(
                    contactDetailsProgressGroup
                )
            }

            contact.photoId?.let {
                contactDetailsPhoto.run {
                    setImageURI(ContactPhotoDelegate.makePhotoUri(it))
                    visibility = View.VISIBLE
                    imageTintList = null
                }
            }

            contactDetailsName.text =
                contact.name ?: getString(R.string.no_name)
            contactDetailsName.visibility = View.VISIBLE

            contactDetailsDescription.text =
                contact.description ?: getString(R.string.no_description)
            contactDetailsDescription.visibility = View.VISIBLE

            val phonesIterator = contact.phoneNumbers.listIterator()
            val emailsIterator = contact.emails.listIterator()

            listOf(
                contactDetailsPhone1,
                contactDetailsPhone2
            ).forEach {
                if (phonesIterator.hasNext()) {
                    it.text = phonesIterator.next()
                    it.visibility = View.VISIBLE
                }
            }

            listOf(
                contactDetailsEmail1,
                contactDetailsEmail2
            ).forEach {
                if (emailsIterator.hasNext()) {
                    it.text = emailsIterator.next()
                    it.visibility = View.VISIBLE
                }
            }

            contact.birthDate?.run {
                contactDetailsBirthDate.visibility = View.VISIBLE
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
                        DateUtils.FORMAT_SHOW_DATE or
                                DateUtils.FORMAT_NO_YEAR
                    )
                )
            }
        }
    }

    private fun updateUI() {
        viewModel.error.observe(viewLifecycleOwner) {
            binding?.contactDetailsContents?.children?.forEach {
                it.visibility = View.GONE
            }
            binding?.contactDetailsErrorGroup?.visibility = View.VISIBLE
        }

        viewModel.contact.observe(viewLifecycleOwner) { contact ->
            updateContact(contact)

            binding?.contactDetailsRefreshView?.isRefreshing = false
        }

        (activity as? ReadContactsPermissionRequester)?.run {
            arguments?.getString(CONTACT_ARG_LOOKUP_KEY)?.let { lookup ->
                requestPermission {
                    viewModel.setContactLookup(lookup)
                }
            }
        }
    }

    private fun refreshContactDetails() {
        arguments?.getString(CONTACT_ARG_LOOKUP_KEY)?.let { lookup ->
            viewModel.refreshContactDetails(lookup)
        }
    }

    private fun initializeRefreshView() {
        binding?.contactDetailsRefreshView?.run {
            setColorSchemeResources(
                R.color.colorPrimary
            )
            setOnRefreshListener {
                refreshContactDetails()
            }
            isEnabled = false
        }
    }
}