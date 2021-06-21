package com.example.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.domain.entities.ContactEntity
import com.example.domain.entities.SimpleContactEntity
import com.example.presentation.R
import com.example.presentation.data.entities.ParcelableSimpleContact
import com.example.presentation.data.entities.toParcelable
import com.example.presentation.data.entities.toSimpleParcelable
import com.example.presentation.ui.adapters.ContactAdapter
import com.example.presentation.ui.adapters.items.ContactListItem
import com.example.presentation.ui.fragments.base.BaseContactListFragment
import com.example.presentation.ui.interfaces.DialogFragmentGateway
import com.example.presentation.ui.interfaces.DialogFragmentGatewayOwner

const val CONTACT_NAVIGATION_LIST_FRAGMENT_TAG =
    "fragment_contact_navigation_list"
const val CONTACT_ARG_ENTITY = "argument_entity"

class ContactNavigationListFragment : BaseContactListFragment() {
    companion object {
        fun newInstance(
            fromContact: ContactEntity
        ) = ContactNavigationListFragment().apply {
            arguments = Bundle().apply {
                putParcelable(
                    CONTACT_ARG_ENTITY,
                    fromContact.toSimpleParcelable()
                )
            }
        }
    }

    override val excludedContactId: Long
        get() = excludedContact.id

    private lateinit var excludedContact: ParcelableSimpleContact
    private var dialogGateway: DialogFragmentGateway? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        excludedContact = arguments?.getParcelable(CONTACT_ARG_ENTITY) ?: run {
            stackGateway?.popBackStack()
            return
        }

        if (context is DialogFragmentGatewayOwner) {
            dialogGateway = context.dialogFragmentGateway
        }
    }

    override fun makeContactAdapter(): ContactAdapter {
        return ContactAdapter { toContact ->
            stackGateway?.navigate(
                excludedContact,
                toContact.toParcelable()
            )
        }
    }

    override fun updateActionBar(actionBar: ActionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setTitle(R.string.navigate)
    }

    override fun updateContactList(list: List<SimpleContactEntity>) {
        if (isSearchQueryEmpty() && list.isEmpty()) {
            dialogGateway?.showGeneralDialog(R.string.set_location_of_another_contact)
            stackGateway?.popBackStack()
        } else {
            viewAdapter?.items = list.map {
                ContactListItem.Entity(it)
            }
        }
    }
}
