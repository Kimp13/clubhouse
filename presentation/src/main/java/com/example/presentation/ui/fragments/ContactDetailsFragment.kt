package com.example.presentation.ui.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.domain.entities.ContactEntity
import com.example.presentation.R
import com.example.presentation.databinding.FragmentContactDetailsBinding
import com.example.presentation.di.interfaces.AppComponentOwner
import com.example.presentation.ui.delegates.ContactPhotoDelegate
import com.example.presentation.ui.interfaces.ContactLocationRetriever
import com.example.presentation.ui.interfaces.ReadContactsPermissionRequester
import com.example.presentation.ui.viewmodels.ContactDetailsViewModel
import javax.inject.Inject

const val CONTACT_DETAILS_FRAGMENT_TAG = "fragment_contact_details"
const val CONTACT_ARG_LOOKUP_KEY = "argument_lookup_key"

class ContactDetailsFragment : Fragment(R.layout.fragment_contact_details) {
    companion object {
        fun newInstance(lookup: String) =
            ContactDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(CONTACT_ARG_LOOKUP_KEY, lookup)
                }
            }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ContactDetailsViewModel by viewModels {
        viewModelFactory
    }
    private var contactEntity: ContactEntity? = null
    private var binding: FragmentContactDetailsBinding? = null
    private var locationRetriever: ContactLocationRetriever? = null
    private var isReminded: Boolean = false
        set(value) {
            field = value

            if (field) {
                viewModel.setReminder()
            } else {
                viewModel.clearReminder()
            }
        }
        get() = viewModel.hasReminder()

    override fun onAttach(context: Context) {
        injectDependencies()

        super.onAttach(context)

        if (context is ContactLocationRetriever) {
            locationRetriever = context
        }
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
            binding?.refreshView?.isRefreshing = true
            refreshContactDetails()

            true
        }
    }

    override fun onDestroyView() {
        binding = null

        super.onDestroyView()
    }

    override fun onDetach() {
        locationRetriever = null

        super.onDetach()
    }

    private fun injectDependencies() {
        (activity?.application as? AppComponentOwner)?.applicationComponent
            ?.contactDetailsFragmentComponent()
            ?.create()
            ?.inject(this)
    }

    private fun updateContact(contact: ContactEntity) {
        contactEntity = contact

        binding?.run {
            if (!refreshView.isEnabled) {
                refreshView.isEnabled = true
                contents.removeView(
                    progressGroup
                )
            }

            photo.run {
                visibility = View.VISIBLE

                contact.photoId?.let {
                    setImageURI(ContactPhotoDelegate.makePhotoUri(it))
                    imageTintList = null
                } ?: run {
                    setImageResource(R.drawable.ic_baseline_person_24)
                    imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                }
            }

            name.text =
                contact.name ?: getString(R.string.no_name)
            name.visibility = View.VISIBLE

            description.text =
                contact.description ?: getString(R.string.no_description)
            description.visibility = View.VISIBLE

            val phonesIterator = contact.phones.listIterator()
            val emailsIterator = contact.emails.listIterator()

            listOf(
                phone1,
                phone2
            ).forEach {
                if (phonesIterator.hasNext()) {
                    it.text = phonesIterator.next()
                    it.visibility = View.VISIBLE
                } else {
                    it.visibility = View.GONE
                }
            }

            listOf(
                email1,
                email2
            ).forEach {
                if (emailsIterator.hasNext()) {
                    it.text = emailsIterator.next()
                    it.visibility = View.VISIBLE
                } else {
                    it.visibility = View.GONE
                }
            }

            contact.birthDate?.run {
                birthDate.visibility = View.VISIBLE
                clarifyRemind.visibility = View.VISIBLE

                remindSwitch.run {
                    visibility = View.VISIBLE
                    isChecked = isReminded

                    setOnCheckedChangeListener { _, isChecked ->
                        isReminded = isChecked
                    }
                }

                birthDate.text = getString(
                    R.string.birthday_fmt,
                    DateUtils.formatDateTime(
                        requireContext(),
                        timeInMillis,
                        DateUtils.FORMAT_SHOW_DATE or
                                DateUtils.FORMAT_NO_YEAR
                    )
                )
            } ?: run {
                birthDate.visibility = View.GONE
                clarifyRemind.visibility = View.GONE
                remindSwitch.visibility = View.GONE
            }

            locationDescription.visibility = View.VISIBLE
            editLocation.visibility = View.VISIBLE
            editLocation.setOnClickListener {
                locationRetriever?.retrieveContactLocation(contact)
            }

            contact.location?.run {
                locationDescription.text = description ?: getString(
                    R.string.location_fmt,
                    latitude,
                    longitude
                )
                editLocation.text = getString(R.string.change)
            } ?: run {
                locationDescription.text = getString(
                    R.string.no_location_set
                )
                editLocation.text = getString(R.string.set)
            }
        }
    }

    private fun updateUI() {
        viewModel.error.observe(viewLifecycleOwner) {
            binding?.contents?.children?.forEach {
                it.visibility = View.GONE
            }
            binding?.errorGroup?.visibility = View.VISIBLE
        }

        viewModel.contact.observe(viewLifecycleOwner) { contact ->
            updateContact(contact)

            binding?.refreshView?.isRefreshing = false
        }

        (activity as? ReadContactsPermissionRequester)?.run {
            arguments?.getString(CONTACT_ARG_LOOKUP_KEY)?.let { lookup ->
                requestContactPermission {
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
        binding?.refreshView?.run {
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