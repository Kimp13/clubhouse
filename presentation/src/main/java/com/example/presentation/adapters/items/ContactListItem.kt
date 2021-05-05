package com.example.presentation.adapters.items

import com.example.domain.entities.SimpleContactEntity

sealed class ContactListItem {
    object Header : ContactListItem()
    object Progress : ContactListItem()
    object Error : ContactListItem()
    data class Entity(val contact: SimpleContactEntity) :
        ContactListItem()

    data class Footer(val count: Int) : ContactListItem()
}