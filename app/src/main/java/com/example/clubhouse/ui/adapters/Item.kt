package com.example.clubhouse.ui.adapters

import com.example.clubhouse.data.SimpleContactEntity

sealed class Item {
    object HeaderItem : Item()
    data class ContactItem(val contact: SimpleContactEntity) : Item()
    data class FooterItem(val count: Int) : Item()
}
