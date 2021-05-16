package com.example.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.presentation.R
import com.example.presentation.databinding.FragmentViewContactLocationBinding
import com.example.presentation.di.interfaces.AppComponentOwner
import com.example.presentation.ui.viewmodels.ViewContactLocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import javax.inject.Inject
import kotlin.math.min

const val VIEW_CONTACT_LOCATION_FRAGMENT_TAG = "fragment_view_contact_location"

private const val BOUNDS_PADDING = 100

class ViewContactLocationFragment :
    Fragment(R.layout.fragment_view_contact_location),
    OnMapReadyCallback {
    companion object {
        fun newInstance(contactId: Long) = ViewContactLocationFragment().apply {
            arguments = Bundle().apply {
                putLong(CONTACT_ARG_ID, contactId)
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var binding: FragmentViewContactLocationBinding? = null
    private val viewModel: ViewContactLocationViewModel by viewModels {
        viewModelFactory
    }

    override fun onAttach(context: Context) {
        injectDependencies()

        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (childFragmentManager.findFragmentById(R.id.map)
                as? SupportMapFragment)?.getMapAsync(this)

        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.view_location)
        }

        binding = FragmentViewContactLocationBinding.bind(view)
    }

    override fun onMapReady(map: GoogleMap?) {
        viewModel.contacts.observe(viewLifecycleOwner) { list ->
            map?.clear()
            binding?.progressBar?.visibility = View.GONE

            val boundsBuilder = LatLngBounds.builder()

            list.forEach { contact ->
                contact.location
                    ?.run {
                        LatLng(
                            latitude,
                            longitude
                        )
                    }
                    ?.let { point ->
                        boundsBuilder.include(point)
                        map?.addMarker(
                            MarkerOptions().position(point)
                                .title(contact.name.let { name ->
                                    contact.location
                                        ?.description
                                        ?.let {
                                            name?.plus("\n$it")
                                        } ?: name
                                })
                        )
                    }
            }

            map?.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    boundsBuilder.build(),
                    BOUNDS_PADDING
                )
            )

            map?.moveCamera(
                CameraUpdateFactory.zoomTo(
                    min(
                        map.maxZoomLevel + MAP_CAMERA_ZOOM_TERM,
                        map.cameraPosition.zoom
                    )
                )
            )
        }

        viewModel.loadContacts(
            arguments?.getLong(CONTACT_ARG_ID)
                ?.takeIf {
                    it > 0
                }
        )
    }

    private fun injectDependencies() {
        (activity?.application as? AppComponentOwner)?.applicationComponent
            ?.viewContactLocationFragmentComponent()
            ?.create()
            ?.inject(this)
    }
}