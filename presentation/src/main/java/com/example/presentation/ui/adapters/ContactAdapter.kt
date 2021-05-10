package com.example.presentation.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.domain.entities.SimpleContactEntity
import com.example.presentation.ui.adapters.items.ContactListItem
import com.example.presentation.ui.delegates.contactEntityDelegate
import com.example.presentation.ui.delegates.contactErrorDelegate
import com.example.presentation.ui.delegates.contactFooterDelegate
import com.example.presentation.ui.delegates.contactHeaderDelegate
import com.example.presentation.ui.delegates.contactProgressDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

object ContactDiffCallback : DiffUtil.ItemCallback<ContactListItem>() {
    override fun areItemsTheSame(
        oldItem: ContactListItem,
        newItem: ContactListItem
    ): Boolean {
        if (
            oldItem is ContactListItem.Entity &&
            newItem is ContactListItem.Entity
        ) {
            return oldItem.contact.lookup == newItem.contact.lookup
        }

        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: ContactListItem,
        newItem: ContactListItem
    ): Boolean {
        if (
            oldItem is ContactListItem.Entity &&
            newItem is ContactListItem.Entity
        ) {
            return oldItem.contact.phoneNumber == newItem.contact.phoneNumber &&
                    oldItem.contact.name == newItem.contact.name
        }

        return oldItem == newItem
    }
}

class ContactAdapter(
    private val clickListener: (SimpleContactEntity) -> Unit
) : AsyncListDifferDelegationAdapter<ContactListItem>(ContactDiffCallback) {
    init {
        delegatesManager.addDelegate(contactHeaderDelegate())
        delegatesManager.addDelegate(contactProgressDelegate())
        delegatesManager.addDelegate(contactErrorDelegate())
        delegatesManager.addDelegate(contactEntityDelegate { position ->
            (items[position] as? ContactListItem.Entity)
                ?.let { item ->
                    clickListener(item.contact)
                }
        })
        delegatesManager.addDelegate(contactFooterDelegate())
    }
}