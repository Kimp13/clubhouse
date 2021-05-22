package com.example.presentation.ui.activities.gateways

import com.example.domain.entities.ContactEntity
import com.example.presentation.data.entities.ParcelableSimpleContact
import com.example.presentation.ui.activities.helpers.FragmentTransactionHelper
import com.example.presentation.ui.fragments.CONTACT_DETAILS_FRAGMENT_TAG
import com.example.presentation.ui.fragments.CONTACT_LOCATION_FRAGMENT_TAG
import com.example.presentation.ui.fragments.CONTACT_NAVIGATION_LIST_FRAGMENT_TAG
import com.example.presentation.ui.fragments.CONTACT_NAVIGATOR_FRAGMENT_TAG
import com.example.presentation.ui.fragments.ContactDetailsFragment
import com.example.presentation.ui.fragments.ContactLocationFragment
import com.example.presentation.ui.fragments.ContactNavigationListFragment
import com.example.presentation.ui.fragments.ContactNavigatorFragment
import com.example.presentation.ui.fragments.REQUEST_READ_CONTACTS_PERMISSION_FRAGMENT_TAG
import com.example.presentation.ui.fragments.RequestReadContactsPermissionFragment
import com.example.presentation.ui.fragments.VIEW_CONTACT_LOCATION_FRAGMENT_TAG
import com.example.presentation.ui.fragments.ViewContactLocationFragment
import com.example.presentation.ui.interfaces.FragmentStackGateway

class FragmentStackGatewayImpl(
    private val transactionHelper: FragmentTransactionHelper
) : FragmentStackGateway {
    override fun onCardClick(lookup: String) {
        transactionHelper.changeFragmentWithTagPushBackStack(
            ContactDetailsFragment.newInstance(lookup),
            CONTACT_DETAILS_FRAGMENT_TAG
        )
    }

    override fun retrieveContactLocation(contact: ContactEntity) {
        transactionHelper.changeFragmentWithTagPushBackStack(
            ContactLocationFragment.newInstance(contact),
            CONTACT_LOCATION_FRAGMENT_TAG
        )
    }

    override fun viewContactLocation(contactEntity: ContactEntity) {
        transactionHelper.changeFragmentWithTagPushBackStack(
            ViewContactLocationFragment.newInstance(contactEntity.id),
            VIEW_CONTACT_LOCATION_FRAGMENT_TAG
        )
    }

    override fun navigateFrom(contact: ContactEntity) {
        transactionHelper.changeFragmentWithTagPushBackStack(
            ContactNavigationListFragment.newInstance(contact),
            CONTACT_NAVIGATION_LIST_FRAGMENT_TAG
        )
    }

    override fun navigate(
        from: ParcelableSimpleContact,
        to: ParcelableSimpleContact
    ) {
        transactionHelper.changeFragmentWithTagPushBackStack(
            ContactNavigatorFragment.newInstance(from, to),
            CONTACT_NAVIGATOR_FRAGMENT_TAG
        )
    }

    override fun viewAllContactsLocation() {
        transactionHelper.changeFragmentWithTagPushBackStack(
            ViewContactLocationFragment(),
            VIEW_CONTACT_LOCATION_FRAGMENT_TAG
        )
    }

    override fun pleadForReadContactsPermission() {
        transactionHelper.changeFragmentWithTagPushBackStack(
            RequestReadContactsPermissionFragment(),
            REQUEST_READ_CONTACTS_PERMISSION_FRAGMENT_TAG
        )
    }

    override fun popBackStack() {
        transactionHelper.popBackStack()
    }
}
