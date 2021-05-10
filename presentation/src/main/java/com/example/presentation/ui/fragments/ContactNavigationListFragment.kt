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
import com.example.presentation.ui.interfaces.ContactLocationNavigator
import com.example.presentation.ui.interfaces.PoppableBackStackOwner

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
    private var locationNavigator: ContactLocationNavigator? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ContactLocationNavigator) {
            locationNavigator = context
        }

        arguments?.getParcelable<ParcelableSimpleContact>(CONTACT_ARG_ENTITY)
            ?.let {
                excludedContact = it
            } ?: (context as? PoppableBackStackOwner)?.popBackStack()
    }

    override fun makeContactAdapter(): ContactAdapter {
        return ContactAdapter { toContact ->
            locationNavigator?.navigate(
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
        viewAdapter?.items = list.map {
            ContactListItem.Entity(it)
        }
    }
}