package com.example.clubhouse.data

import android.content.ContentUris
import android.content.Context
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val SELECTION = "${ContactsContract.Data.LOOKUP_KEY} = ?"
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

private const val SIMPLE_DATA_SELECTION =
    "${ContactsContract.Data.MIMETYPE} in ('${
        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
    }', '${
        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
    }') and ${
        ContactsContract.Data.LOOKUP_KEY
    } = ?"
private val DATA_PROJECTION = arrayOf(
    ContactsContract.Data.MIMETYPE,
    ContactsContract.Data.DATA1,
    ContactsContract.Data.DATA2,
    ContactsContract.CommonDataKinds.Photo.PHOTO_FILE_ID
)
private const val DATA_SORT_ORDER = "${ContactsContract.Data.MIMETYPE} asc"
private const val DATA_MIMETYPE_INDEX = 0
private const val DATA_FIELD_INDEX = 1
private const val DATA_ADDITIONAL_FIELD_INDEX = 2
private const val DATA_PHOTO_ID_INDEX = 3

object ContactRepository {
    fun makePhotoUri(photoId: Long) =
        ContentUris.withAppendedId(
            ContactsContract.DisplayPhoto.CONTENT_URI,
            photoId
        )

    suspend fun getSimpleContacts(
        context: Context
    ): List<SimpleContactEntity>? = withContext(Dispatchers.IO) {
        context.contentResolver?.let { contentResolver ->
            val contacts = mutableListOf<SimpleContactEntity>()

            contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                CONTACT_PROJECTION,
                null,
                null,
                CONTACT_SORT_ORDER
            )?.use {
                if (it.moveToFirst()) {
                    do {
                        contacts.add(
                            SimpleContactEntity(
                                it.getLong(CONTACT_ID_INDEX),
                                it.getString(CONTACT_LOOKUP_INDEX),
                                it.getString(CONTACT_NAME_INDEX)
                            )
                        )
                    } while (it.moveToNext())
                }
            }

            List(contacts.size) { i ->
                launch {
                    contentResolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        DATA_PROJECTION,
                        SIMPLE_DATA_SELECTION,
                        arrayOf(contacts[i].lookup),
                        DATA_SORT_ORDER
                    )?.use { it ->
                        if (it.moveToFirst()) {
                            do {
                                when (it.getString(DATA_MIMETYPE_INDEX)) {
                                    ContactsContract
                                        .CommonDataKinds
                                        .Phone
                                        .CONTENT_ITEM_TYPE -> {
                                        it.getString(
                                            DATA_FIELD_INDEX
                                        )?.let { phone ->
                                            contacts[i].phoneNumber = phone
                                        }
                                    }
                                    ContactsContract
                                        .CommonDataKinds
                                        .Photo
                                        .CONTENT_ITEM_TYPE -> {
                                        contacts[i].photoId = it.getLong(
                                            DATA_PHOTO_ID_INDEX
                                        )
                                    }
                                }
                            } while (it.moveToNext())
                        }
                    }
                }
            }.joinAll()

            contacts
        }
    }

    suspend fun getContacts(
        context: Context,
        lookups: List<String?>
    ): List<ContactEntity> = withContext(Dispatchers.IO) {
        val contacts: MutableList<ContactEntity?> = MutableList(lookups.size) { null }

        List(lookups.size) { i ->
            launch {
                contacts[i] = lookups[i]?.let {
                    getContact(
                        context,
                        it
                    )
                }
            }
        }.joinAll()

        contacts.filterNotNull()
    }

    suspend fun getContact(
        context: Context,
        lookup: String
    ) = withContext(Dispatchers.IO) {
        context.contentResolver?.let { contentResolver ->
            var id: Long = 0
            var name: String? = null
            var description: String? = null
            var birthDate: BirthDate? = null
            var photoId: Long? = null
            val emails = mutableListOf<String>()
            val phones = mutableListOf<String>()

            listOf(
                launch {
                    contentResolver.query(
                        ContactsContract.Contacts.CONTENT_URI,
                        CONTACT_PROJECTION,
                        SELECTION,
                        arrayOf(lookup),
                        CONTACT_SORT_ORDER
                    )?.use {
                        if (it.moveToFirst()) {
                            id = it.getLong(CONTACT_ID_INDEX)
                        }
                    }
                },
                launch {
                    contentResolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        DATA_PROJECTION,
                        SELECTION,
                        arrayOf(lookup),
                        null
                    )?.use {
                        if (it.moveToFirst()) {
                            do {
                                when (it.getString(DATA_MIMETYPE_INDEX)) {
                                    ContactsContract
                                        .CommonDataKinds
                                        .StructuredName
                                        .CONTENT_ITEM_TYPE -> {
                                        name = it.getString(
                                            DATA_FIELD_INDEX
                                        )
                                    }
                                    ContactsContract
                                        .CommonDataKinds
                                        .Email
                                        .CONTENT_ITEM_TYPE -> {
                                        it.getString(
                                            DATA_FIELD_INDEX
                                        )?.let { email ->
                                            emails.add(email)
                                        }
                                    }
                                    ContactsContract
                                        .CommonDataKinds
                                        .Phone
                                        .CONTENT_ITEM_TYPE -> {
                                        it.getString(
                                            DATA_FIELD_INDEX
                                        )?.let { phone ->
                                            phones.add(phone)
                                        }
                                    }
                                    ContactsContract
                                        .CommonDataKinds
                                        .Note
                                        .CONTENT_ITEM_TYPE -> {
                                        description = it.getString(DATA_FIELD_INDEX)
                                    }
                                    ContactsContract
                                        .CommonDataKinds
                                        .Event
                                        .CONTENT_ITEM_TYPE -> {
                                        if (it.getInt(
                                                DATA_ADDITIONAL_FIELD_INDEX
                                            ) == ContactsContract
                                                .CommonDataKinds
                                                .Event
                                                .TYPE_BIRTHDAY
                                        ) {
                                            it.getString(DATA_FIELD_INDEX)
                                                ?.split("-")
                                                ?.reversed()
                                                ?.let { date ->
                                                    birthDate = BirthDate(
                                                        date[0].toInt(),
                                                        date[1].toInt() - 1
                                                    )
                                                }
                                        }
                                    }
                                    ContactsContract
                                        .CommonDataKinds
                                        .Photo
                                        .CONTENT_ITEM_TYPE -> {
                                        photoId = it.getLong(
                                            DATA_PHOTO_ID_INDEX
                                        )
                                    }
                                }
                            } while (it.moveToNext())
                        }
                    }
                }
            ).joinAll()

            ContactEntity(
                id, lookup, name, phones, emails, description, birthDate, photoId
            )
        }
    }
}