package com.example.presentation.ui.fragments

import androidx.appcompat.app.ActionBar
import com.example.domain.entities.SimpleContactEntity
import com.example.presentation.R
import com.example.presentation.ui.adapters.ContactAdapter
import com.example.presentation.ui.adapters.items.ContactListItem
import com.example.presentation.ui.fragments.base.BaseContactListFragment

const val CONTACT_LIST_FRAGMENT_TAG = "fragment_contact_list"

class ContactListFragment : BaseContactListFragment() {
    override val excludedContactId: Long? = null

    override fun updateActionBar(actionBar: ActionBar) {
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.setTitle(R.string.contact_list)
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

    override fun makeContactAdapter() = ContactAdapter(
        {
            gateway?.viewAllContactsLocation()
        }
    ) {
        gateway?.onCardClick(it.lookup)
    }
}
