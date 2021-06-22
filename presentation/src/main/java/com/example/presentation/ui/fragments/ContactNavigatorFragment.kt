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
import com.example.presentation.data.toLatLng
import com.example.presentation.databinding.FragmentViewContactLocationBinding
import com.example.presentation.di.interfaces.AppComponentOwner
import com.example.presentation.ui.fragments.helpers.ContactRouteDelegate
import com.example.presentation.ui.fragments.helpers.GoogleMapHelper
import com.example.presentation.ui.fragments.interfaces.GoogleMapHelpee
import com.example.presentation.ui.interfaces.DialogFragmentGateway
import com.example.presentation.ui.interfaces.DialogFragmentGatewayOwner
import com.example.presentation.ui.interfaces.FragmentStackGateway
import com.example.presentation.ui.interfaces.FragmentStackGatewayOwner
import com.example.presentation.ui.viewmodels.ContactNavigatorViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import javax.inject.Inject

const val CONTACT_NAVIGATOR_FRAGMENT_TAG = "fragment_contact_navigator"
const val CONTACT_ARG_ENTITIES_PAIR = "argument_entities_pair"

class ContactNavigatorFragment :
    Fragment(R.layout.fragment_view_contact_location),
    OnMapReadyCallback,
    GoogleMapHelpee {
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
    private var mapHelper: GoogleMapHelper? = null
    private var routeDelegate: ContactRouteDelegate? = null
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
                routeDelegate = ContactRouteDelegate(contacts)
            }
            ?: stackGateway?.popBackStack()
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
        mapHelper = GoogleMapHelper(map, this)

        viewModel.steps.observe(viewLifecycleOwner) { list ->
            if (list == null || list.size < 2) {
                dialogGateway?.showGeneralDialog(R.string.navigation_sorry, R.string.ok)
                stackGateway?.popBackStack()
            } else {
                binding?.progressBar?.visibility = View.GONE

                buildRoute(list)
            }
        }

        viewModel.getSteps(contacts)
    }

    override fun onDestroyView() {
        mapHelper = null
        binding = null

        super.onDestroyView()
    }

    override fun onDetach() {
        stackGateway = null

        super.onDetach()
    }

    override fun getMapPadding() = resources.getDimensionPixelOffset(
        R.dimen.mapBoundsPadding
    )

    private fun buildRoute(list: List<LocationEntity>) = routeDelegate?.run {
        mapHelper?.buildRouteBetweenPoints(
            getFirstPointWithDescription(list),
            getLastPointWithDescription(list),
            list.map(LocationEntity::toLatLng)
        )
    }

    private fun injectDependencies() {
        (activity?.application as? AppComponentOwner)?.applicationComponent
            ?.contactNavigatorFragmentComponent()
            ?.create()
            ?.inject(this)
    }
}
