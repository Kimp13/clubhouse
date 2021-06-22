package com.example.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.domain.entities.ContactEntity
import com.example.presentation.R
import com.example.presentation.databinding.FragmentContactDetailsBinding
import com.example.presentation.di.interfaces.AppComponentOwner
import com.example.presentation.ui.fragments.helpers.ContactDetailsBindingOwner
import com.example.presentation.ui.interfaces.FragmentStackGateway
import com.example.presentation.ui.interfaces.FragmentStackGatewayOwner
import com.example.presentation.ui.interfaces.PermissionGateway
import com.example.presentation.ui.interfaces.PermissionGatewayOwner
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
    private var bindingOwner: ContactDetailsBindingOwner? = null
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

        bindingOwner = ContactDetailsBindingOwner(FragmentContactDetailsBinding.bind(view))
        bindingOwner?.initializeRefreshViewWithRefreshListener(::refreshContactDetails)

        setHasOptionsMenu(true)
        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.fragment_contact_details_menu, menu)
        menu.findItem(R.id.menuRefresh).setOnMenuItemClickListener {
            bindingOwner?.showRefreshing()
            refreshContactDetails()

            true
        }
    }

    override fun onDestroyView() {
        bindingOwner = null

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

        bindingOwner?.run {
            updateContactDetails(contact)
            setHasBirthdayNotificationsOn(isReminded)

            setBirthdaySubscriptionChangeListener { _, isChecked ->
                isReminded = isChecked
            }

            setEditLocationListener {
                stackGateway?.retrieveContactLocation(contact)
            }

            setViewLocationListener {
                stackGateway?.viewContactLocation(contact)
            }

            setNavigateListener {
                stackGateway?.navigateFrom(contact)
            }
        }
    }

    private fun updateUI() {
        viewModel.error.observe(viewLifecycleOwner) {
            bindingOwner?.showError()
        }

        viewModel.contact.observe(viewLifecycleOwner) { contact ->
            updateContact(contact)
            bindingOwner?.stopShowingRefreshing()
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
}
