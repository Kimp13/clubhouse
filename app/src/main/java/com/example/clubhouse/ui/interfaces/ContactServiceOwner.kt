package com.example.clubhouse.ui.interfaces

import com.example.clubhouse.ui.services.ContactService

interface ContactServiceOwner {
    fun getService(): ContactService?
}