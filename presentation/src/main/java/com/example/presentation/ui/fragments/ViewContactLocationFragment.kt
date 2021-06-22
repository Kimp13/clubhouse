package com.example.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.domain.entities.ContactEntity
import com.example.presentation.R
import com.example.presentation.data.entities.withDescription
import com.example.presentation.databinding.FragmentViewContactLocationBinding
import com.example.presentation.di.interfaces.AppComponentOwner
import com.example.presentation.ui.fragments.helpers.GoogleMapHelper
import com.example.presentation.ui.fragments.interfaces.GoogleMapHelpee
import com.example.presentation.ui.interfaces.DialogFragmentGateway
import com.example.presentation.ui.interfaces.DialogFragmentGatewayOwner
import com.example.presentation.ui.interfaces.FragmentStackGateway
import com.example.presentation.ui.interfaces.FragmentStackGatewayOwner
import com.example.presentation.ui.viewmodels.ViewContactLocationViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import javax.inject.Inject

const val VIEW_CONTACT_LOCATION_FRAGMENT_TAG = "fragment_view_contact_location"

class ViewContactLocationFragment :
    Fragment(R.layout.fragment_view_contact_location),
    OnMapReadyCallback,
    GoogleMapHelpee {
    companion object {
        fun newInstance(contactId: Long) = ViewContactLocationFragment().apply {
            arguments = Bundle().apply {
                putLong(CONTACT_ARG_ID, contactId)
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var stackGateway: FragmentStackGateway? = null
    private var dialogGateway: DialogFragmentGateway? = null
    private var binding: FragmentViewContactLocationBinding? = null
    private var mapHelper: GoogleMapHelper? = null
    private val viewModel: ViewContactLocationViewModel by viewModels {
        viewModelFactory
    }

    override fun onAttach(context: Context) {
        injectDependencies()

        super.onAttach(context)

        if (context is FragmentStackGatewayOwner) {
            stackGateway = context.stackGateway
        }

        if (context is DialogFragmentGatewayOwner) {
            dialogGateway = context.dialogFragmentGateway
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerMapReadyCallback()
        editActionBar()

        binding = FragmentViewContactLocationBinding.bind(view)
    }

    override fun onMapReady(map: GoogleMap?) {
        mapHelper = GoogleMapHelper(map, this)

        arguments?.getLong(CONTACT_ARG_ID)
            ?.takeUnless { it == 0L }
            ?.let { id ->
                val fallbackMessage = R.string.set_location_of_the_contact

                viewModel.contacts.observe(viewLifecycleOwner) {
                    processContactListWithFallbackMessage(it, fallbackMessage)
                }

                viewModel.loadContact(id)
            }
            ?: run {
                val fallbackMessage = R.string.set_location_of_one_contact

                viewModel.contacts.observe(viewLifecycleOwner) {
                    processContactListWithFallbackMessage(it, fallbackMessage)
                }

                viewModel.loadAllContacts()
            }
    }

    override fun onDestroyView() {
        mapHelper = null

        super.onDestroyView()
    }

    override fun getMapPadding() = resources.getDimensionPixelOffset(
        R.dimen.mapBoundsPadding
    )

    private fun injectDependencies() {
        (activity?.application as? AppComponentOwner)?.applicationComponent
            ?.viewContactLocationFragmentComponent()
            ?.create()
            ?.inject(this)
    }

    private fun registerMapReadyCallback() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun editActionBar() {
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.view_location)
        }
    }

    private fun processContactListWithFallbackMessage(
        contactList: List<ContactEntity>,
        @StringRes message: Int
    ) {
        binding?.progressBar?.visibility = View.GONE

        val markerPoints = contactList.mapNotNull { contact ->
            val description = contact.name
                ?.let { name ->
                    contact.location
                        ?.description
                        ?.let { description ->
                            "$name: $description"
                        }
                        ?: name
                }

            description?.let {
                contact.location?.withDescription(it)
            }
        }

        if (markerPoints.isEmpty()) {
            popBackDueToIllegalState(message)
        } else {
            mapHelper?.addAndCenterMarkersWithDescription(markerPoints)
        }
    }

    private fun popBackDueToIllegalState(@StringRes message: Int) {
        dialogGateway?.showGeneralDialog(message)
        stackGateway?.popBackStack()
    }
}
