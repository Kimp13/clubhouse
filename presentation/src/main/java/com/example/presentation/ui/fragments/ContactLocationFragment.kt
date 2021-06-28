package com.example.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.domain.entities.ContactEntity
import com.example.presentation.R
import com.example.presentation.data.entities.ParcelableContactLocation
import com.example.presentation.data.entities.toParcelable
import com.example.presentation.databinding.FragmentContactLocationBinding
import com.example.presentation.di.interfaces.AppComponentOwner
import com.example.presentation.ui.fragments.helpers.ContactLocationBindingOwner
import com.example.presentation.ui.fragments.helpers.ContactLocationMapOwner
import com.example.presentation.ui.interfaces.DialogFragmentGateway
import com.example.presentation.ui.interfaces.DialogFragmentGatewayOwner
import com.example.presentation.ui.interfaces.FragmentStackGateway
import com.example.presentation.ui.interfaces.FragmentStackGatewayOwner
import com.example.presentation.ui.interfaces.PermissionGateway
import com.example.presentation.ui.interfaces.PermissionGatewayOwner
import com.example.presentation.ui.viewmodels.ContactLocationViewModel
import com.example.presentation.ui.viewmodels.states.ContactLocationState
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

const val CONTACT_LOCATION_FRAGMENT_TAG = "fragment_contact_location"
const val CONTACT_ARG_ID = "argument_id"
const val CONTACT_ARG_LOCATION = "argument_location"

class ContactLocationFragment :
    Fragment(R.layout.fragment_contact_location),
    OnMapReadyCallback {
    companion object {
        fun newInstance(contact: ContactEntity) =
            ContactLocationFragment().apply {
                arguments = Bundle().apply {
                    putString(CONTACT_ARG_LOOKUP_KEY, contact.lookup)
                    putLong(CONTACT_ARG_ID, contact.id)

                    contact.location?.let {
                        putParcelable(
                            CONTACT_ARG_LOCATION,
                            it.toParcelable()
                        )
                    }
                }
            }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ContactLocationViewModel by viewModels {
        viewModelFactory
    }
    private var bindingOwner: ContactLocationBindingOwner? = null
    private var mapOwner: ContactLocationMapOwner? = null
    private var stackGateway: FragmentStackGateway? = null
    private var permissionGateway: PermissionGateway? = null
    private var dialogGateway: DialogFragmentGateway? = null

    override fun onAttach(context: Context) {
        injectDependencies()

        super.onAttach(context)

        if (context is FragmentStackGatewayOwner) {
            stackGateway = context.stackGateway
        }

        if (context is PermissionGatewayOwner) {
            permissionGateway = context.permissionGateway
        }

        if (context is DialogFragmentGatewayOwner) {
            dialogGateway = context.dialogFragmentGateway
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkControlsClarification()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        (activity as? AppCompatActivity)?.supportActionBar?.run {
            setTitle(R.string.edit_location)
            setDisplayHomeAsUpEnabled(true)
        }

        bindingOwner = ContactLocationBindingOwner(FragmentContactLocationBinding.bind(view))
    }

    override fun onMapReady(map: GoogleMap?) {
        mapOwner = ContactLocationMapOwner(map)
        mapOwner?.setOnMapClickListener(::changeSelectedPoint)

        setUpFooter()

        val currentPoint = viewModel.currentPoint
            ?: arguments?.getParcelable<ParcelableContactLocation>(CONTACT_ARG_LOCATION)
                ?.run {
                    LatLng(latitude, longitude)
                }

        if (currentPoint == null) {
            permissionGateway?.requestLocationPermission { isGranted ->
                if (isGranted) {
                    observeLocation()
                }
            }
        } else {
            mapOwner?.centerMap(currentPoint)
            changeSelectedPoint(currentPoint)
        }
    }

    override fun onDestroyView() {
        bindingOwner = null
        mapOwner = null

        super.onDestroyView()
    }

    override fun onDetach() {
        stackGateway = null
        permissionGateway = null

        super.onDetach()
    }

    private fun changeSelectedPoint(point: LatLng?) {
        mapOwner?.changeSelectedPoint(point)

        if (point == null) {
            bindingOwner?.noAddressAvailable()
        } else {
            bindingOwner?.showProgress()
            viewModel.currentPoint = point
        }
    }

    private fun observeLocation() {
        viewModel.state.observe(
            viewLifecycleOwner,
            object : Observer<ContactLocationState> {
                override fun onChanged(state: ContactLocationState) {
                    state.location?.let {
                        if (!viewModel.interacted) {
                            val point = LatLng(it.latitude, it.longitude)

                            mapOwner?.centerMap(point)
                            changeSelectedPoint(point)
                        }

                        viewModel.state.removeObserver(this)
                    }
                }
            }
        )

        viewModel.initLocation()
    }

    private fun setUpFooter() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.progress) {
                bindingOwner?.showProgress()
            } else {
                val address = state.address ?: viewModel.currentPoint?.let {
                    getString(R.string.location_fmt, it.latitude, it.longitude)
                }

                if (address == null) {
                    bindingOwner?.noAddressAvailable()
                } else {
                    bindingOwner?.setLocationDescription(address)
                }
            }
        }

        bindingOwner?.setOnSubmitListener {
            arguments?.getLong(CONTACT_ARG_ID)?.let { id ->
                viewModel.submit(id)

                viewModel.state.observe(viewLifecycleOwner) { state ->
                    if (state.locationWritten) {
                        stackGateway?.popBackStack()
                    }
                }
            }
        }
    }

    private fun checkControlsClarification() {
        viewModel.state.observe(
            viewLifecycleOwner,
            object : Observer<ContactLocationState> {
                override fun onChanged(state: ContactLocationState?) {
                    state?.areMapControlsClarified
                        ?.takeUnless { it }
                        ?.let { _ ->
                            dialogGateway?.showGeneralDialog(
                                R.string.clarify_card_controls_text,
                                R.string.clarify_card_controls_ok
                            )

                            viewModel.writeMapControlsClarified()
                            viewModel.state.removeObserver(this)
                        }
                }
            }
        )

        viewModel.checkIfMapControlsClarified()
    }

    private fun injectDependencies() {
        (activity?.application as? AppComponentOwner)?.applicationComponent
            ?.contactLocationFragmentComponent()
            ?.create()
            ?.inject(this)
    }
}
