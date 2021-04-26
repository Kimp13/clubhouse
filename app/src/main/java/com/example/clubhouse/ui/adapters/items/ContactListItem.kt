package com.example.clubhouse.ui.adapters.items

import com.example.clubhouse.data.entities.SimpleContactEntity

sealed class ContactListItem {
    object Header : ContactListItem()
    object Progress : ContactListItem()
    object Error : ContactListItem()
    data class Entity(val contact: SimpleContactEntity) : ContactListItem()
    data class Footer(val count: Int) : ContactListItem()
}