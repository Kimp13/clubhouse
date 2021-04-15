package com.example.clubhouse.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.provider.ContactsContract
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import io.reactivex.rxjava3.core.Single

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

private const val CONTACT_NO_ID = -1L

object ContactRepository {
    fun makePhotoUri(photoId: Long) =
        ContentUris.withAppendedId(
            ContactsContract.DisplayPhoto.CONTENT_URI,
            photoId
        )

    fun getSimpleContacts(
        context: Context,
        query: String?
    ): Single<List<SimpleContactEntity>> {
        return Single.fromCallable {
            context.contentResolver?.let { resolver ->
                val contacts = getContactListFramework(query, resolver)
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
            } ?: throw makeContentResolverError()
        }
    }

    fun getContacts(
        context: Context,
        lookups: List<String?>
    ): Single<List<ContactEntity>> {
        return Single.merge(
            lookups.filterNotNull().map { lookup ->
                getContact(
                    context,
                    lookup
                )
            }
        )
            .toList()
    }

    fun getContact(
        context: Context,
        lookup: String
    ): Single<ContactEntity> {
        return Single.fromCallable {
            var id = CONTACT_NO_ID
            var name: String? = null
            var description: String? = null
            var birthDate: BirthDate? = null
            var photoId: Long? = null
            val emails = mutableListOf<String>()
            val phones = mutableListOf<String>()

            context.contentResolver?.let { contentResolver ->
                contentResolver.query(
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
                                    it.getLong(
                                        DATA_PHOTO_ID_INDEX
                                    ).let { rowPhotoId ->
                                        if (rowPhotoId > 0) {
                                            photoId = rowPhotoId
                                        }
                                    }

                                }
                            }
                        } while (it.moveToNext())
                    }
                }

                if (id == CONTACT_NO_ID) {
                    throw IllegalStateException("Contact id can't be null.")
                }

                ContactEntity(
                    id,
                    lookup,
                    name,
                    description,
                    birthDate,
                    photoId,
                    emails,
                    phones
                )
            } ?: throw makeContentResolverError()
        }
    }

    private fun makeContentResolverError() = IllegalStateException(
        "ContentResolver was found out to be null :("
    )

    private fun getContactListFramework(
        query: String?,
        contentResolver: ContentResolver
    ): List<SimpleContactEntity> {
        val contactList = mutableListOf<SimpleContactEntity>()

        val (selection, selectionArgs) = if (query == null || query.isBlank()) {
            null to null
        } else {
            SIMPLE_CONTACT_SELECTION to arrayOf("%${query}%")
        }

        contentResolver.query(
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
}