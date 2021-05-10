package com.example.presentation.data.repositories

import android.content.ContentResolver
import android.content.Context
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

private const val SIMPLE_CONTACT_SELECTION =
    "${ContactsContract.Contacts.DISPLAY_NAME} like ?"

private const val SIMPLE_DATA_SELECTION =
    "${ContactsContract.Data.MIMETYPE} in ('${
        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
    }', '${
        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
    }') and ${
        ContactsContract.Data.LOOKUP_KEY
    } in "
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
    override suspend fun getSimpleContacts(query: String?):
            List<SimpleContactEntity>? = withContext(Dispatchers.IO) {
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
                        }?.let(fun(i: Int) {
                            when (it.getStringOrNull(DATA_MIMETYPE_INDEX)) {
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
                                            contacts[i].photoId = rowPhotoId
                                        }
                                    }
                                }
                            }
                        })
                    } while (it.moveToNext())
                }
            }

            contacts
        }
    }

    override suspend fun getContacts(lookups: List<String?>):
            List<ContactEntity> = withContext(Dispatchers.IO) {
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

    override suspend fun getContact(lookup: String) =
        withContext(Dispatchers.IO) {
            context.contentResolver?.let { resolver ->
                getContactId(resolver, lookup)?.let { id ->
                    var name: String? = null
                    var description: String? = null
                    var birthDate: BirthDate? = null
                    var photoId: Long? = null
                    val emails = mutableListOf<String>()
                    val phones = mutableListOf<String>()

                    resolver.query(
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
                                        it.getStringOrNull(
                                            DATA_FIELD_INDEX
                                        )?.let { newName ->
                                            name = newName
                                        }
                                    }

                                    ContactsContract
                                        .CommonDataKinds
                                        .Email
                                        .CONTENT_ITEM_TYPE -> {
                                        it.getStringOrNull(
                                            DATA_FIELD_INDEX
                                        )?.let { email ->
                                            emails.add(email)
                                        }
                                    }

                                    ContactsContract
                                        .CommonDataKinds
                                        .Phone
                                        .CONTENT_ITEM_TYPE -> {
                                        it.getStringOrNull(
                                            DATA_FIELD_INDEX
                                        )?.let { phone ->
                                            phones.add(phone)
                                        }
                                    }

                                    ContactsContract
                                        .CommonDataKinds
                                        .Note
                                        .CONTENT_ITEM_TYPE -> {
                                        it.getStringOrNull(
                                            DATA_FIELD_INDEX
                                        )?.let { newDescription ->
                                            description = newDescription
                                        }
                                    }

                                    ContactsContract
                                        .CommonDataKinds
                                        .Event
                                        .CONTENT_ITEM_TYPE -> {
                                        it.getIntOrNull(
                                            DATA_ADDITIONAL_FIELD_INDEX
                                        )?.takeIf { type ->
                                            type == ContactsContract
                                                .CommonDataKinds
                                                .Event
                                                .TYPE_BIRTHDAY
                                        }?.let { _ ->
                                            it.getStringOrNull(
                                                DATA_FIELD_INDEX
                                            )
                                                ?.split("-")
                                                ?.reversed()
                                                ?.let { date ->
                                                    birthDate =
                                                        BirthDate(
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
                                        it.getLongOrNull(
                                            DATA_PHOTO_ID_INDEX
                                        )?.takeIf { rowPhotoId ->
                                            rowPhotoId > 0
                                        }?.let { rowPhotoId ->
                                            photoId = rowPhotoId
                                        }

                                    }
                                }
                            } while (it.moveToNext())
                        }
                    }

                    ContactEntity(
                        id = id,
                        lookup = lookup,
                        name = name,
                        description = description,
                        birthDate = birthDate,
                        photoId = photoId,
                        phones = phones,
                        emails = emails
                    )
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
            SIMPLE_CONTACT_SELECTION to arrayOf("%${query}%")
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
            SELECTION,
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
}