package com.example.presentation.delegates

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.presentation.R
import com.example.presentation.adapters.items.ContactListItem
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate

fun contactHeaderDelegate() =
    adapterDelegate<ContactListItem.Header, ContactListItem>(
        R.layout.contact_list_header
    ) {}

fun contactProgressDelegate() =
    adapterDelegate<ContactListItem.Progress, ContactListItem>(
        R.layout.progress_card
    ) {}

fun contactErrorDelegate() =
    adapterDelegate<ContactListItem.Error, ContactListItem>(
        R.layout.error_card
    ) {}

fun contactEntityDelegate(
    clickListener: (Int) -> Unit
) = adapterDelegate<ContactListItem.Entity, ContactListItem>(
    R.layout.contact_card
) {
    val contactPhoto: ImageView = findViewById(R.id.contactCardPhoto)
    val contactName: TextView = findViewById(R.id.contactCardName)
    val contactPhone: TextView = findViewById(R.id.contactCardPhone)

    itemView.setOnClickListener {
        val position = adapterPosition

        if (position != RecyclerView.NO_POSITION) {
            clickListener(position)
        }
    }

    bind {
        contactPhoto.run {
            item.contact.photoId?.let {
                setImageURI(ContactPhotoDelegate.makePhotoUri(it))
                imageTintList = null
            } ?: run {
                setImageResource(R.drawable.ic_baseline_person_24)
                imageTintList = ColorStateList.valueOf(
                    getColor(R.color.colorPrimary)
                )
            }
        }

        contactName.text = item.contact.name ?: getString(
            R.string.no_name
        )
        contactPhone.text = item.contact.phoneNumber ?: getString(
            R.string.no_phone_number
        )
    }
}

fun contactFooterDelegate() =
    adapterDelegate<ContactListItem.Footer, ContactListItem>(
        R.layout.contact_list_footer
    ) {
        val textView: TextView = findViewById(
            R.id.contactListFooterTextView
        )

        bind {
            textView.text = itemView.context.getString(
                R.string.total_contacts,
                item.count
            )
        }
    }