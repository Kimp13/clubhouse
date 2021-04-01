package com.example.clubhouse.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.clubhouse.data.SimpleContactEntity
import com.example.clubhouse.ui.delegates.ContactDelegate
import com.example.clubhouse.ui.delegates.ContactEntityDelegate
import com.example.clubhouse.ui.delegates.ContactFooterDelegate
import com.example.clubhouse.ui.delegates.ContactHeaderDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

object ContactDiffCallback : DiffUtil.ItemCallback<ContactDelegate>() {
    override fun areItemsTheSame(
        oldItem: ContactDelegate,
        newItem: ContactDelegate
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: ContactDelegate,
        newItem: ContactDelegate
    ): Boolean {
        if (
            oldItem is ContactEntityDelegate &&
            newItem is ContactEntityDelegate
        ) {
            return (
                    oldItem.contact?.phoneNumber ==
                            newItem.contact?.phoneNumber &&
                            oldItem.contact?.name ==
                            newItem.contact?.name
                    )
        }

        return oldItem::class == newItem::class
    }
}

class ContactAdapter(
    private val clickListener: (SimpleContactEntity) -> Unit
) : AsyncListDifferDelegationAdapter<ContactDelegate>(ContactDiffCallback) {
    private val getContentsCount: () -> Int = {
        itemCount - 2
    }

    init {
        delegatesManager.addDelegate(ContactHeaderDelegate.create())
        delegatesManager.addDelegate(ContactEntityDelegate().create())
        delegatesManager.addDelegate(
            ContactFooterDelegate(
                getContentsCount
            ).create()
        )
    }

    fun setContactList(items: List<SimpleContactEntity>) {
        val differList = mutableListOf<ContactDelegate>(
            ContactHeaderDelegate
        )

        items.forEach {
            differList.add(ContactEntityDelegate(
                it
            ) {
                clickListener(it)
            })
        }

        differList.add(ContactFooterDelegate(getContentsCount))

        setItems(differList)

        notifyItemChanged(itemCount - 1)
    }
}