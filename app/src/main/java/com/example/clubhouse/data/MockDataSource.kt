package com.example.clubhouse.data

object MockDataSource {
    private const val name = "Имя"
    private const val firstPhoneNumber = "89000000000"

    fun getSimpleContacts(): List<SimpleContactEntity> {
        return listOf(SimpleContactEntity(name, firstPhoneNumber))
    }

    fun getContacts(ids: IntArray): List<ContactEntity> {
        return listOf(getContact(1))
    }

    fun getContact(id: Int): ContactEntity {
        return ContactEntity(
            1,
            name,
            listOf(firstPhoneNumber, "89123456789"),
            listOf("email@for.test", "another@one.email"),
            "Описание контакта",
            BirthDate(28, 5)
        )
    }
}