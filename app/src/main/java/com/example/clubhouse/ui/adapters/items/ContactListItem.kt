package com.example.clubhouse.ui.adapters.items

import com.example.clubhouse.data.SimpleContactEntity

sealed class ContactListItem {
    object Header : ContactListItem()
    data class Entity(val contact: SimpleContactEntity) : ContactListItem()
    data class Footer(val count: Int) : ContactListItem()
}