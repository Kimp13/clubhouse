package com.example.clubhouse.data

object MockDataSource {
    private const val name = "Имя"
    private const val firstPhoneNumber = "89000000000"

    fun getContacts(): List<SimpleContactEntity> {
        return listOf(SimpleContactEntity(name, firstPhoneNumber))
    }

    fun getContact(id: Int): ContactEntity {
        return ContactEntity(
            name,
            listOf(firstPhoneNumber, "89123456789"),
            listOf("email@for.test", "another@one.email"),
            "Описание контакта"
        )
    }
}