package com.example.presentation.data.repositories

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.example.domain.entities.BirthDate
import com.example.domain.entities.ContactEntity
import com.example.domain.entities.SimpleContactEntity
import com.example.domain.repositories.interfaces.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

private const val LOOKUP_SELECTION =
    "${ContactsContract.Contacts.LOOKUP_KEY} = ?"
private const val ID_SELECTION = "${ContactsContract.Contacts._ID} = ?"
private val CONTACT_PROJECTION = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
)
private const val CONTACT_SORT_ORDER =
    "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} asc"
private const val CONTACT_ID_INDEX = 0
private const val CONTACT_LOOKUP_INDEX = 1
private const val CONTACT_NAME_INDEX = 2

private const val SIMPLE_CONTACT_SELECTION =
    "${ContactsContract.Contacts.DISPLAY_NAME} like ?"

private const val SIMPLE_DATA_SELECTION =
    "${ContactsContract.Data.MIMETYPE} in ('" +
        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE +
        ", '${ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE}" +
        "') and ${ContactsContract.Data.LOOKUP_KEY} in "
private val DATA_PROJECTION = arrayOf(
    ContactsContract.Data.MIMETYPE,
    ContactsContract.Data.LOOKUP_KEY,
    ContactsContract.Data.DATA1,
    ContactsContract.Data.DATA2,
    ContactsContract.CommonDataKinds.Photo.PHOTO_FILE_ID
)
private const val DATA_SORT_ORDER = "${ContactsContract.Data.MIMETYPE} asc"
private const val DATA_MIMETYPE_INDEX = 0
private const val DATA_LOOKUP_INDEX = 1
private const val DATA_FIELD_INDEX = 2
private const val DATA_ADDITIONAL_FIELD_INDEX = 3
private const val DATA_PHOTO_ID_INDEX = 4

class ContactProviderRepository(
    private val context: Context
) : ContactRepository {
    override suspend fun getSimpleContacts(query: String?) =
        withContext(Dispatchers.IO) {
            context.contentResolver?.let { resolver ->
                val contacts = getContactListFramework(resolver, query)
                val lookupToIndex = contacts.withIndex().associate {
                    it.value.lookup to it.index
                }
                val selection = SIMPLE_DATA_SELECTION +
                    lookupToIndex.keys.joinToString(
                        prefix = "(",
                        postfix = ")"
                    ) { "?" }

                resolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    DATA_PROJECTION,
                    selection,
                    lookupToIndex.keys.toTypedArray(),
                    DATA_SORT_ORDER
                )?.use {
                    if (it.moveToFirst()) {
                        do {
                            it.getStringOrNull(DATA_LOOKUP_INDEX)?.let { key ->
                                lookupToIndex[key]
                            }?.let(
                                fun(i: Int) {
                                    when (
                                        it.getStringOrNull(DATA_MIMETYPE_INDEX)
                                    ) {
                                        ContactsContract
                                            .CommonDataKinds
                                            .Phone
                                            .CONTENT_ITEM_TYPE -> {
                                            it.getStringOrNull(
                                                DATA_FIELD_INDEX
                                            )?.let { phone ->
                                                contacts[i].phoneNumber = phone
                                            }
                                        }

                                        ContactsContract
                                            .CommonDataKinds
                                            .Photo
                                            .CONTENT_ITEM_TYPE -> {
                                            it.getLongOrNull(
                                                DATA_PHOTO_ID_INDEX
                                            )?.let { rowPhotoId ->
                                                if (rowPhotoId > 0) {
                                                    contacts[i].photoId =
                                                        rowPhotoId
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        } while (it.moveToNext())
                    }
                }

                contacts
            }
        }

    override suspend fun getContacts(lookups: List<String?>) =
        withContext(Dispatchers.IO) {
            lookups.mapNotNull {
                it?.let {
                    async {
                        getContact(it)
                    }
                }
            }
                .awaitAll()
                .filterNotNull()
        }

    override suspend fun findContactsById(ids: List<Long>) =
        withContext(Dispatchers.IO) {
            ids.map {
                async {
                    findContactById(it)
                }
            }
                .awaitAll()
                .filterNotNull()
        }

    override suspend fun getContact(lookup: String) =
        withContext(Dispatchers.IO) {
            context.contentResolver?.let { resolver ->
                getContactId(resolver, lookup)?.let { id ->
                    populateContactEntity(resolver, ContactEntity(id, lookup))
                }
            }
        }

    override suspend fun findContactById(id: Long) =
        withContext(Dispatchers.IO) {
            context.contentResolver?.let { resolver ->
                getContactLookup(resolver, id)?.let { lookup ->
                    populateContactEntity(resolver, ContactEntity(id, lookup))
                }
            }
        }

    private fun getContactListFramework(
        resolver: ContentResolver,
        query: String?
    ): List<SimpleContactEntity> {
        val contactList = mutableListOf<SimpleContactEntity>()

        val (selection, selectionArgs) = if (query == null || query.isBlank()) {
            null to null
        } else {
            SIMPLE_CONTACT_SELECTION to arrayOf("%$query%")
        }

        resolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            CONTACT_PROJECTION,
            selection,
            selectionArgs,
            CONTACT_SORT_ORDER
        )?.use {
            if (it.moveToFirst()) {
                do {
                    contactList.add(
                        SimpleContactEntity(
                            it.getLong(CONTACT_ID_INDEX),
                            it.getString(CONTACT_LOOKUP_INDEX),
                            it.getString(CONTACT_NAME_INDEX)
                        )
                    )
                } while (it.moveToNext())
            }
        }

        return contactList
    }

    private fun getContactId(
        resolver: ContentResolver,
        lookup: String
    ): Long? {
        var id: Long? = null

        resolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            CONTACT_PROJECTION,
            LOOKUP_SELECTION,
            arrayOf(lookup),
            CONTACT_SORT_ORDER
        )?.use {
            if (it.moveToFirst()) {
                it.getLongOrNull(CONTACT_ID_INDEX)?.let { newId ->
                    id = newId
                }
            }
        }

        return id
    }

    private fun getContactLookup(
        resolver: ContentResolver,
        id: Long
    ): String? {
        var lookup: String? = null

        resolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            CONTACT_PROJECTION,
            ID_SELECTION,
            arrayOf(id.toString()),
            CONTACT_SORT_ORDER
        )?.use {
            if (it.moveToFirst()) {
                it.getStringOrNull(CONTACT_LOOKUP_INDEX)?.let { newLookup ->
                    lookup = newLookup
                }
            }
        }

        return lookup
    }

    private fun populateContactEntity(
        resolver: ContentResolver,
        contactEntityFramework: ContactEntity
    ): ContactEntity {
        var result = contactEntityFramework
        val scheme = getPopulationScheme()

        resolver.query(
            ContactsContract.Data.CONTENT_URI,
            DATA_PROJECTION,
            LOOKUP_SELECTION,
            arrayOf(contactEntityFramework.lookup),
            null
        )?.use {
            if (it.moveToFirst()) {
                do {
                    result = scheme[it.getStringOrNull(DATA_MIMETYPE_INDEX)]
                        ?.invoke(it, result)
                        ?: result
                } while (it.moveToNext())
            }
        }

        return result
    }

    private fun getPopulationScheme(): HashMap<String, (Cursor, ContactEntity) -> ContactEntity> =
        hashMapOf(
            ContactsContract.CommonDataKinds.StructuredName
                .CONTENT_ITEM_TYPE to { cursor, contact ->
                cursor.getStringOrNull(DATA_FIELD_INDEX)?.let {
                    contact.copy(name = it)
                } ?: contact
            },

            ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE to { cursor, contact ->
                cursor.getStringOrNull(DATA_FIELD_INDEX)?.let {
                    contact.copy(emails = contact.emails.plus(it))
                } ?: contact
            },

            ContactsContract.CommonDataKinds.Phone
                .CONTENT_ITEM_TYPE to { cursor, contact ->
                cursor.getStringOrNull(DATA_FIELD_INDEX)?.let {
                    contact.copy(phones = contact.phones.plus(it))
                } ?: contact
            },

            ContactsContract.CommonDataKinds.Note
                .CONTENT_ITEM_TYPE to { cursor, contact ->
                cursor.getStringOrNull(DATA_FIELD_INDEX)?.let {
                    contact.copy(description = it)
                } ?: contact
            },

            ContactsContract.CommonDataKinds.Event
                .CONTENT_ITEM_TYPE to { cursor, contact ->
                cursor.getIntOrNull(DATA_ADDITIONAL_FIELD_INDEX)
                    ?.takeIf {
                        it == ContactsContract.CommonDataKinds.Event
                            .TYPE_BIRTHDAY
                    }
                    ?.let { _ ->
                        cursor.getStringOrNull(DATA_FIELD_INDEX)
                            ?.split("-")
                            ?.reversed()
                            ?.let {
                                contact.copy(
                                    birthDate = BirthDate(
                                        it[0].toInt(),
                                        it[1].toInt() - 1
                                    )
                                )
                            }
                    } ?: contact
            },

            ContactsContract.CommonDataKinds.Photo
                .CONTENT_ITEM_TYPE to { cursor, contact ->
                cursor.getLongOrNull(DATA_PHOTO_ID_INDEX)
                    ?.takeIf { it > 0 }
                    ?.let {
                        contact.copy(photoId = it)
                    } ?: contact
            }
        )
}
