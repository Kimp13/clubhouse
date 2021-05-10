package com.example.presentation.ui.fragments

import android.content.Context
import androidx.appcompat.app.ActionBar
import com.example.domain.entities.SimpleContactEntity
import com.example.presentation.R
import com.example.presentation.ui.adapters.ContactAdapter
import com.example.presentation.ui.adapters.items.ContactListItem
import com.example.presentation.ui.fragments.base.BaseContactListFragment
import com.example.presentation.ui.interfaces.ContactCardClickListener
import com.example.presentation.ui.interfaces.ContactLocationViewer

const val CONTACT_LIST_FRAGMENT_TAG = "fragment_contact_list"

class ContactListFragment : BaseContactListFragment() {
    override val excludedContactId: Long? = null

    private var cardClickListener: ContactCardClickListener? = null
    private var locationViewer: ContactLocationViewer? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ContactCardClickListener) {
            cardClickListener = context
        }

        if (context is ContactLocationViewer) {
            locationViewer = context
        }
    }

    override fun updateActionBar(actionBar: ActionBar) {
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.setTitle(R.string.contact_list)
    }

    override fun onDetach() {
        cardClickListener = null
        locationViewer = null

        super.onDetach()
    }

    override fun updateContactList(list: List<SimpleContactEntity>) {
        viewAdapter?.items = listOf(
            ContactListItem.Header
        ) + list.map {
            ContactListItem.Entity(it)
        } + listOf(
            ContactListItem.Footer(list.size)
        )
    }

    override fun makeContactAdapter() = ContactAdapter({
        locationViewer?.viewAllContactsLocation()
    }) {
        cardClickListener?.onCardClick(it.lookup)
    }
}