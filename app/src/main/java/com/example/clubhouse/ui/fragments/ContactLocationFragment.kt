package com.example.clubhouse.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.clubhouse.R
import com.example.clubhouse.data.entities.ContactEntity
import com.example.clubhouse.data.entities.ContactLocation
import com.example.clubhouse.databinding.FragmentContactLocationBinding
import com.example.clubhouse.ui.activities.COMMON_SHARED_PREFERENCES_KEY
import com.example.clubhouse.ui.interfaces.AccessLocationPermissionRequester
import com.example.clubhouse.ui.interfaces.PoppableBackStackOwner
import com.example.clubhouse.ui.viewmodels.ContactLocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import timber.log.Timber

const val CONTACT_LOCATION_FRAGMENT_TAG = "fragment_contact_location"
const val CONTACT_ARG_ID = "argument_id"
const val CONTACT_ARG_LOCATION = "argument_location"

private const val BELOVED_COMPANY_LATITUDE = 56.8463985
private const val BELOVED_COMPANY_LONGITUDE = 53.2332288
private const val ARE_MAP_CONTROLS_CLARIFIED_KEY = "are_map_controls_clarified?"

class ContactLocationFragment :
    Fragment(R.layout.fragment_contact_location),
    OnMapReadyCallback {
    companion object {
        fun newInstance(contact: ContactEntity) =
            ContactLocationFragment().apply {
                arguments = Bundle().apply {
                    putString(CONTACT_ARG_LOOKUP_KEY, contact.lookup)
                    putLong(CONTACT_ARG_ID, contact.id)
                    putParcelable(CONTACT_ARG_LOCATION, contact.location)
                }
            }
    }

    private val viewModel: ContactLocationViewModel by viewModels()
    private var binding: FragmentContactLocationBinding? = null
    private var permissionRequester: AccessLocationPermissionRequester? = null
    private var backStackOwner: PoppableBackStackOwner? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is AccessLocationPermissionRequester) {
            permissionRequester = context
        }

        if (context is PoppableBackStackOwner) {
            backStackOwner = context
        }

        checkClarification(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (childFragmentManager.findFragmentById(R.id.contactLocationMap)
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
        makeMapResponsive(map)
        updateUI()
        updateMap(map)

        viewModel.currentPoint?.let {
            map?.addMarker(MarkerOptions().position(it))
        } ?: arguments?.getParcelable<ContactLocation>(
            CONTACT_ARG_LOCATION
        )?.let {
            val point = LatLng(
                it.latitude,
                it.longitude
            )

            updateMap(map, point)
            onNewPoint(map, point)
        } ?: permissionRequester?.requestLocationPermission { isGranted ->
            if (isGranted) {
                viewModel.location.observe(viewLifecycleOwner) {
                    it?.let {
                        if (!viewModel.interacted) {
                            val point = LatLng(
                                it.latitude,
                                it.longitude
                            )

                            updateMap(map, point)
                            onNewPoint(map, point)
                        }
                    }
                }
            }
        }

    }

    override fun onDetach() {
        permissionRequester = null
        backStackOwner = null

        super.onDetach()
    }

    private fun onNewPoint(
        map: GoogleMap?,
        point: LatLng?
    ) {
        map?.clear()

        point?.let {
            map?.addMarker(MarkerOptions().position(it))

            binding?.contactLocationTextView?.visibility = View.GONE
            binding?.contactLocationSubmit?.visibility = View.GONE
            binding?.contactLocationProgressBar?.visibility = View.VISIBLE

            viewModel.currentPoint = it
        } ?: onNoAddressAvailable()
    }

    private fun makeMapResponsive(
        map: GoogleMap?
    ) {
        map?.setOnMapClickListener {
            onNewPoint(map, it)
        }
    }

    private fun updateMap(
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
        binding?.contactLocationTextView?.visibility = View.VISIBLE
        binding?.contactLocationSubmit?.visibility = View.GONE
        binding?.contactLocationProgressBar?.visibility = View.GONE

        binding?.contactLocationTextView?.text = getString(
            R.string.no_location_set
        )
    }

    private fun updateSubmit() {
        binding?.contactLocationSubmit?.setOnClickListener {
            it?.visibility = View.GONE
            binding?.contactLocationProgressBar?.visibility = View.VISIBLE

            lifecycleScope.launch {
                try {
                    arguments?.getLong(CONTACT_ARG_ID)?.let { id ->
                        viewModel.submit(id)
                    }
                } catch (e: CancellationException) {
                    Timber.d("ContactLocationFragment job cancelled\n$e")
                }
            }

            backStackOwner?.popBackStack()
        }
    }

    private fun updateUI() {
        updateSubmit()

        viewModel.address.observe(viewLifecycleOwner) { address ->
            val text = address ?: viewModel.currentPoint?.let { point ->
                resources.getString(
                    R.string.location_fmt,
                    point.latitude,
                    point.longitude
                )
            }

            text?.let {
                binding?.contactLocationTextView?.visibility = View.VISIBLE
                binding?.contactLocationSubmit?.visibility = View.VISIBLE
                binding?.contactLocationProgressBar?.visibility = View.GONE

                binding?.contactLocationTextView?.text = it
            } ?: onNoAddressAvailable()
        }

        viewModel.initLocation()
    }

    private fun checkClarification(context: Context) {
        context.getSharedPreferences(
            COMMON_SHARED_PREFERENCES_KEY,
            Context.MODE_PRIVATE
        )?.run {
            if (
                !getBoolean(
                    ARE_MAP_CONTROLS_CLARIFIED_KEY,
                    false
                )
            ) {
                ClarifyMapControlsFragment().show(
                    childFragmentManager,
                    null
                )

                edit().putBoolean(
                    ARE_MAP_CONTROLS_CLARIFIED_KEY,
                    true
                )
                    .apply()
            }
        }
    }
}