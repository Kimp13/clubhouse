package com.example.clubhouse.ui.delegates

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.TextView
import com.example.clubhouse.R
import com.example.clubhouse.data.ContactRepository
import com.example.clubhouse.data.SimpleContactEntity
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate

sealed class ContactDelegate {
    abstract fun create(): AdapterDelegate<List<ContactDelegate>>
}

object ContactHeaderDelegate : ContactDelegate() {
    override fun create() =
        adapterDelegate<ContactHeaderDelegate, ContactDelegate>(
            R.layout.contact_list_header
        ) {
            /* no-op */
        }

    override fun equals(other: Any?) = other is ContactHeaderDelegate
}

class ContactEntityDelegate(
    val contact: SimpleContactEntity? = null,
    private val clickListener: (() -> Unit)? = null
) : ContactDelegate() {
    override fun create() =
        adapterDelegate<ContactEntityDelegate, ContactDelegate>(
            R.layout.contact_card
        ) {
            val contactPhoto: ImageView = findViewById(R.id.contactCardPhoto)
            val contactName: TextView = findViewById(R.id.contactCardName)
            val contactPhone: TextView = findViewById(R.id.contactCardPhone)

            itemView.setOnClickListener {
                item.clickListener?.invoke()
            }

            bind {
                contactPhoto.run {
                    item.contact?.photoId?.let {
                        setImageURI(ContactRepository.makePhotoUri(it))
                        imageTintList = null
                    } ?: run {
                        setImageResource(R.drawable.ic_baseline_person_24)
                        imageTintList = ColorStateList.valueOf(
                            getColor(R.color.colorPrimary)
                        )
                    }
                }

                contactName.text = item.contact?.name ?: getString(
                    R.string.no_name
                )
                contactPhone.text = item.contact?.phoneNumber ?: getString(
                    R.string.no_phone_number
                )
            }
        }

    override fun hashCode() = contact.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other is ContactEntityDelegate) {
            return other.contact?.lookup == contact?.lookup
        }

        return false
    }
}

class ContactFooterDelegate(
    private val getItemCount: () -> Int
) : ContactDelegate() {
    override fun create() =
        adapterDelegate<ContactFooterDelegate, ContactDelegate>(
            R.layout.contact_list_footer
        ) {
            val textView: TextView = findViewById(
                R.id.contactListFooterTextView
            )

            bind {
                textView.text = itemView.context.getString(
                    R.string.total_contacts,
                    item.getItemCount()
                )
            }
        }

    override fun equals(other: Any?) = other.hashCode() == hashCode()

    override fun hashCode() = getItemCount.hashCode()
}
