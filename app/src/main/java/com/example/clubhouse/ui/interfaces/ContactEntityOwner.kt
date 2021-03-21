package com.example.clubhouse.ui.interfaces

import com.example.clubhouse.data.ContactEntity

interface ContactEntityOwner {
    fun getContact(): ContactEntity?
}