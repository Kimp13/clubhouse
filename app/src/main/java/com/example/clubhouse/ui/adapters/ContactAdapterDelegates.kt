package com.example.clubhouse.ui.adapters

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.example.clubhouse.R
import com.example.clubhouse.data.ContactRepository
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate

fun headerAdapterDelegate() =
    adapterDelegate<Item.HeaderItem, Item>(R.layout.item_header) {}

fun contactAdapterDelegate(onItemClick: (Int) -> Unit) =
    adapterDelegate<Item.ContactItem, Item>(R.layout.contact_card) {
        val contactPhoto: ImageView = findViewById(R.id.contactCardPhoto)
        val contactName: TextView = findViewById(R.id.contactCardName)
        val contactPhone: TextView = findViewById(R.id.contactCardPhone)

        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != NO_POSITION) {
                onItemClick(position)
            }
        }

        bind {
            with(item) {
                contactPhoto.run {
                    contact.photoId?.let {
                        setImageURI(ContactRepository.makePhotoUri(it))
                        imageTintList = null
                    } ?: run {
                        setImageResource(R.drawable.ic_baseline_person_24)
                        imageTintList = ColorStateList.valueOf(
                            getColor(R.color.colorPrimary)
                        )
                    }
                }
                contactName.text = contact.name ?: getString(
                    R.string.no_name
                )
                contactPhone.text = contact.phoneNumber ?: getString(
                    R.string.no_phone_number
                )
            }
        }
    }

fun footerAdapterDelegate() =
    adapterDelegate<Item.FooterItem, Item>(R.layout.item_footer) {
        val footerText = findViewById<TextView>(R.id.footer)

        bind {
            footerText.text = item.count.toString()
        }
    }