package com.example.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.domain.entities.LocationEntity
import com.example.presentation.R
import com.example.presentation.data.entities.ParcelableSimpleContact
import com.example.presentation.databinding.FragmentViewContactLocationBinding
import com.example.presentation.di.interfaces.AppComponentOwner
import com.example.presentation.ui.interfaces.DialogFragmentGateway
import com.example.presentation.ui.interfaces.DialogFragmentGatewayOwner
import com.example.presentation.ui.interfaces.FragmentStackGateway
import com.example.presentation.ui.interfaces.FragmentStackGatewayOwner
import com.example.presentation.ui.viewmodels.ContactNavigatorViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import javax.inject.Inject

const val CONTACT_NAVIGATOR_FRAGMENT_TAG = "fragment_contact_navigator"
const val CONTACT_ARG_ENTITIES_PAIR = "argument_entities_pair"

class ContactNavigatorFragment :
    Fragment(R.layout.fragment_view_contact_location),
    OnMapReadyCallback {
    companion object {
        fun newInstance(
            from: ParcelableSimpleContact,
            to: ParcelableSimpleContact
        ) = ContactNavigatorFragment().apply {
            arguments = Bundle().apply {
                putParcelableArray(CONTACT_ARG_ENTITIES_PAIR, arrayOf(from, to))
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ContactNavigatorViewModel by viewModels {
        viewModelFactory
    }

    private var binding: FragmentViewContactLocationBinding? = null
    private var stackGateway: FragmentStackGateway? = null
    private var dialogGateway: DialogFragmentGateway? = null
    private lateinit var contacts: Pair<ParcelableSimpleContact, ParcelableSimpleContact>

    override fun onAttach(context: Context) {
        injectDependencies()

        super.onAttach(context)

        if (context is FragmentStackGatewayOwner) {
            stackGateway = context.stackGateway
        }

        if (context is DialogFragmentGatewayOwner) {
            dialogGateway = context.dialogFragmentGateway
        }

        arguments?.getParcelableArray(CONTACT_ARG_ENTITIES_PAIR)
            ?.mapNotNull {
                it as? ParcelableSimpleContact
            }
            ?.takeIf { it.size == 2 }
            ?.let {
                contacts = it[0] to it[1]
            } ?: stackGateway?.popBackStack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        (activity as? AppCompatActivity)?.supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.route)
        }

        binding = FragmentViewContactLocationBinding.bind(view)
    }

    override fun onMapReady(map: GoogleMap?) {
        viewModel.steps.observe(viewLifecycleOwner) { nullableList ->
            nullableList?.let { list ->
                map?.clear()
                addMarkers(map, list)

                binding?.progressBar?.visibility = View.GONE

                val boundsBuilder = LatLngBounds.Builder()
                var polylineOptions = PolylineOptions()

                list.forEach {
                    val point = LatLng(
                        it.latitude,
                        it.longitude
                    )

                    boundsBuilder.include(point)
                    polylineOptions = polylineOptions.add(point)
                }

                map?.addPolyline(polylineOptions)
                map?.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        boundsBuilder.build(),
                        resources.getDimensionPixelOffset(
                            R.dimen.mapBoundsPadding
                        )
                    )
                )
            } ?: run {
                dialogGateway?.showGeneralDialog(R.string.navigation_sorry, R.string.ok)
                stackGateway?.popBackStack()
            }
        }

        viewModel.getSteps(contacts)
    }

    override fun onDestroyView() {
        binding = null

        super.onDestroyView()
    }

    override fun onDetach() {
        stackGateway = null

        super.onDetach()
    }

    private fun addMarkers(
        map: GoogleMap?,
        list: List<LocationEntity>
    ) {
        map?.addMarker(
            MarkerOptions().position(
                LatLng(
                    list.first().latitude,
                    list.first().longitude
                )
            ).title(contacts.first.name)
        )

        map?.addMarker(
            MarkerOptions().position(
                LatLng(
                    list.last().latitude,
                    list.last().longitude
                )
            ).title(contacts.second.name)
        )
    }

    private fun injectDependencies() {
        (activity?.application as? AppComponentOwner)?.applicationComponent
            ?.contactNavigatorFragmentComponent()
            ?.create()
            ?.inject(this)
    }
}
