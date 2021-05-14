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
import com.example.presentation.ui.interfaces.AccessLocationPermissionRequester
import com.example.presentation.ui.interfaces.PoppableBackStackOwner
import com.example.presentation.ui.viewmodels.ContactLocationViewModel
import com.example.presentation.ui.viewmodels.states.ContactLocationState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import javax.inject.Inject

const val CONTACT_LOCATION_FRAGMENT_TAG = "fragment_contact_location"
const val CONTACT_ARG_ID = "argument_id"
const val CONTACT_ARG_LOCATION = "argument_location"

private const val BELOVED_COMPANY_LATITUDE = 56.8463985
private const val BELOVED_COMPANY_LONGITUDE = 53.2332288

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
    private var binding: FragmentContactLocationBinding? = null
    private var permissionRequester: AccessLocationPermissionRequester? = null
    private var backStackOwner: PoppableBackStackOwner? = null

    override fun onAttach(context: Context) {
        injectDependencies()

        super.onAttach(context)

        if (context is AccessLocationPermissionRequester) {
            permissionRequester = context
        }

        if (context is PoppableBackStackOwner) {
            backStackOwner = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkControlsClarification()

        (childFragmentManager.findFragmentById(R.id.map)
                as? SupportMapFragment)?.getMapAsync(this)

        (activity as? AppCompatActivity)?.supportActionBar?.run {
            setTitle(
                R.string.contact_location
            )
            setDisplayHomeAsUpEnabled(true)
        }

        binding = FragmentContactLocationBinding.bind(view)

    }

    override fun onMapReady(map: GoogleMap?) {
        observeAddress()
        makeSubmitClickable()
        makeMapClickable(map)
        centerMap(map)

        viewModel.currentPoint?.let {
            map?.addMarker(MarkerOptions().position(it))
        } ?: arguments?.getParcelable<ParcelableContactLocation>(
            CONTACT_ARG_LOCATION
        )?.let {
            val point = LatLng(
                it.latitude,
                it.longitude
            )

            centerMap(map, point)
            changeSelectedPoint(map, point)
        } ?: permissionRequester?.requestLocationPermission { isGranted ->
            if (isGranted) {
                observeLocation(map)
            }
        }
    }

    override fun onDetach() {
        permissionRequester = null
        backStackOwner = null

        super.onDetach()
    }

    private fun injectDependencies() {
        (activity?.application as? AppComponentOwner)?.applicationComponent
            ?.contactLocationFragmentComponent()
            ?.create()
            ?.inject(this)
    }

    private fun changeSelectedPoint(
        map: GoogleMap?,
        point: LatLng?
    ) {
        map?.clear()

        point?.let {
            map?.addMarker(MarkerOptions().position(it))

            binding?.locationDescription?.visibility = View.GONE
            binding?.submit?.visibility = View.GONE
            binding?.progressBar?.visibility = View.VISIBLE

            viewModel.currentPoint = it
        } ?: onNoAddressAvailable()
    }

    private fun makeMapClickable(
        map: GoogleMap?
    ) {
        map?.setOnMapClickListener {
            changeSelectedPoint(map, it)
        }
    }

    private fun centerMap(
        map: GoogleMap?,
        latLng: LatLng? = null
    ) {
        val point = latLng ?: viewModel.currentPoint ?: LatLng(
            BELOVED_COMPANY_LATITUDE,
            BELOVED_COMPANY_LONGITUDE
        )

        map?.run {
            moveCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(point)
                        .zoom(maxZoomLevel - 7.5F)
                        .build()
                )
            )
        }
    }

    private fun onNoAddressAvailable() {
        binding?.locationDescription?.visibility = View.VISIBLE
        binding?.submit?.visibility = View.GONE
        binding?.progressBar?.visibility = View.GONE

        binding?.locationDescription?.text = getString(
            R.string.no_location_set
        )
    }

    private fun indicateProgress() {
        binding?.locationDescription?.visibility = View.GONE
        binding?.submit?.visibility = View.GONE
        binding?.progressBar?.visibility = View.VISIBLE
    }

    private fun setLocationString(location: String) {
        binding?.locationDescription?.visibility = View.VISIBLE
        binding?.submit?.visibility = View.VISIBLE
        binding?.progressBar?.visibility = View.GONE

        binding?.locationDescription?.text = location
    }

    private fun makeSubmitClickable() {
        binding?.submit?.setOnClickListener {
            arguments?.getLong(CONTACT_ARG_ID)?.let { id ->
                viewModel.submit(id)

                viewModel.state.observe(viewLifecycleOwner) { state ->
                    if (state.locationWritten) {
                        backStackOwner?.popBackStack()
                    }
                }
            }
        }
    }

    private fun observeLocation(map: GoogleMap?) {
        viewModel.state.observe(
            viewLifecycleOwner,
            object : Observer<ContactLocationState> {
                override fun onChanged(state: ContactLocationState) {
                    state.location?.let {
                        if (!viewModel.interacted) {
                            val point = LatLng(
                                it.latitude,
                                it.longitude
                            )

                            centerMap(map, point)
                            changeSelectedPoint(map, point)
                        }

                        viewModel.state.removeObserver(this)
                    }
                }
            }
        )

        viewModel.initLocation()
    }

    private fun observeAddress() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.progress) {
                indicateProgress()
            } else {
                (state.address ?: viewModel.currentPoint?.let {
                    getString(
                        R.string.location_fmt,
                        it.latitude,
                        it.longitude
                    )
                })?.let {
                    setLocationString(it)
                } ?: onNoAddressAvailable()
            }
        }
    }

    private fun checkControlsClarification() {
        viewModel.state.observe(
            viewLifecycleOwner,
            object : Observer<ContactLocationState> {
                override fun onChanged(state: ContactLocationState?) {
                    state?.areMapControlsClarified
                        ?.takeIf { !it }
                        ?.let {
                            ClarifyMapControlsFragment().show(
                                childFragmentManager,
                                null
                            )

                            viewModel.writeMapControlsClarified()
                            viewModel.state.removeObserver(this)
                        }
                }
            }
        )

        viewModel.checkIfMapControlsClarified()
    }
}