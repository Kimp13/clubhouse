package com.example.clubhouse.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.clubhouse.data.SimpleContactEntity
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

object ContactDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
        when {
            oldItem is Item.ContactItem && newItem is Item.ContactItem -> oldItem.contact.id == newItem.contact.id
            else -> oldItem == newItem
        }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
        when {
            oldItem is Item.ContactItem && newItem is Item.ContactItem ->
                oldItem.contact.phoneNumber == newItem.contact.phoneNumber
                    && oldItem.contact.name == newItem.contact.name
            else -> oldItem == newItem
        }
}

class ContactAdapter(
    private val onContactClick: (SimpleContactEntity) -> Unit
) : AsyncListDifferDelegationAdapter<Item>(ContactDiffCallback) {
    init {
        delegatesManager
            .addDelegate(headerAdapterDelegate())
            .addDelegate(contactAdapterDelegate { position ->
                val item = items[position]
                if (item is Item.ContactItem) {
                    onContactClick(item.contact)
                }
            })
            .addDelegate(footerAdapterDelegate())
    }
}
