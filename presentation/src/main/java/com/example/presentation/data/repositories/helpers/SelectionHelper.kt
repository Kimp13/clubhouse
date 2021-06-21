package com.example.presentation.data.repositories.helpers

import android.provider.ContactsContract

private const val SIMPLE_CONTACT_SELECTION =
    "${ContactsContract.Contacts.DISPLAY_NAME} like ?"
private const val CONTACT_DATA_SELECTION =
    "${ContactsContract.Data.MIMETYPE} in ('" +
        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE +
        "', '${ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE}" +
        "') and ${ContactsContract.Data.LOOKUP_KEY} in "

class SelectionHelper {
    fun contactDataSelectionFromMap(map: Map<*, *>) = contactDataSelectionFromIterable(map.values)

    fun contactDataSelectionFromIterable(iterable: Iterable<*>) = buildString {
        append(CONTACT_DATA_SELECTION)
        append(selectionInClauseFromIterable(iterable))
    }

    fun contactSelectionFromContactsIdsAndQuery(
        contactsIds: Collection<Long>?,
        query: String?
    ) = buildString {
        val querySpecified = query != null && query.isNotBlank()

        if (contactsIds != null) {
            val idColumn = ContactsContract.Contacts._ID
            val idValues = selectionInClauseFromIterable(contactsIds)

            append("$idColumn in $idValues")

            if (querySpecified) {
                append(" and ")
            }
        }

        if (querySpecified) {
            append(SIMPLE_CONTACT_SELECTION)
        }
    }

    fun contactSelectionArgsFromContactsIdsAndQuery(
        contactsIds: Collection<Long>?,
        query: String?
    ): Array<String> {
        val contactIdInClauseArgs = contactsIds?.map { it.toString() } ?: emptyList()

        return if (query != null && query.isNotBlank()) {
            contactIdInClauseArgs.plus(query).toTypedArray()
        } else {
            contactIdInClauseArgs.toTypedArray()
        }
    }

    private fun selectionInClauseFromIterable(iterable: Iterable<*>) = buildString {
        append('(')

        var hasMarksBefore = false
        iterable.forEach { _ ->
            if (hasMarksBefore) {
                append(',')
            } else {
                hasMarksBefore = true
            }

            append('?')
        }

        append(')')
    }
}
