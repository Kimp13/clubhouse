package com.example.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.domain.entities.ContactEntity
import com.example.presentation.R
import com.example.presentation.databinding.FragmentContactDetailsBinding
import com.example.presentation.di.interfaces.AppComponentOwner
import com.example.presentation.ui.interfaces.FragmentStackGateway
import com.example.presentation.ui.interfaces.FragmentStackGatewayOwner
import com.example.presentation.ui.interfaces.PermissionGateway
import com.example.presentation.ui.interfaces.PermissionGatewayOwner
import com.example.presentation.ui.viewmodels.ContactDetailsViewModel
import com.example.presentation.ui.views.hide
import com.example.presentation.ui.views.setContents
import com.example.presentation.ui.views.setPhotoId
import com.example.presentation.ui.views.show
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
    private var stackGateway: FragmentStackGateway? = null
    private var permissionGateway: PermissionGateway? = null
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

        if (context is FragmentStackGatewayOwner) {
            stackGateway = context.stackGateway
        }

        if (context is PermissionGatewayOwner) {
            permissionGateway = context.permissionGateway
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
        stackGateway = null
        permissionGateway = null

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
                contents.removeView(progressGroup)
            }

            photo.setPhotoId(contact.photoId)
            name.setContents(contact.name ?: getString(R.string.no_name))
            description.setContents(
                contact.description ?: getString(R.string.no_description)
            )
            listOf(phone1, phone2).setContents(contact.phones.iterator())
            listOf(email1, email2).setContents(contact.emails.iterator())

            contact.birthDate?.run {
                remindSwitch.let {
                    listOf(birthDate, clarifyRemind, it).show()

                    it.isChecked = isReminded
                    it.setOnCheckedChangeListener { _, isChecked ->
                        isReminded = isChecked
                    }
                }

                birthDate.text = getString(
                    R.string.birthday_fmt,
                    DateUtils.formatDateTime(
                        context,
                        timeInMillis,
                        DateUtils.FORMAT_SHOW_DATE or
                            DateUtils.FORMAT_NO_YEAR
                    )
                )
            } ?: run {
                listOf(birthDate, clarifyRemind, remindSwitch).hide()
            }

            listOf(locationDescription, editLocation).show()
            editLocation.setOnClickListener {
                stackGateway?.retrieveContactLocation(contact)
            }
            viewLocation.setOnClickListener {
                stackGateway?.viewContactLocation(contact)
            }
            navigate.setOnClickListener {
                stackGateway?.navigateFrom(contact)
            }

            contact.location?.run {
                locationDescription.text = description ?: getString(
                    R.string.location_fmt,
                    latitude,
                    longitude
                )
                listOf(viewLocation, navigate).show()
            } ?: run {
                locationDescription.text = getString(
                    R.string.no_location_set
                )
                listOf(viewLocation, navigate).hide()
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

        arguments?.getString(CONTACT_ARG_LOOKUP_KEY)?.let { lookup ->
            permissionGateway?.requestContactPermission {
                viewModel.setContactLookup(lookup)
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
