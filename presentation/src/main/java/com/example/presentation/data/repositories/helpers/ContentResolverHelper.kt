package com.example.presentation.data.repositories.helpers

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.example.domain.entities.ContactEntity
import java.lang.ref.WeakReference

private val DATA_PROJECTION = arrayOf(
    ContactsContract.Data.MIMETYPE,
    ContactsContract.Data.LOOKUP_KEY,
    ContactsContract.Data.DATA1,
    ContactsContract.Data.DATA2,
    ContactsContract.CommonDataKinds.Photo.PHOTO_FILE_ID
)
private const val DATA_SORT_ORDER = "${ContactsContract.Data.MIMETYPE} asc"

private val CONTACT_PROJECTION = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
)
private const val CONTACT_SORT_ORDER =
    "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} asc"

private const val LOOKUP_SELECTION = "${ContactsContract.Contacts.LOOKUP_KEY} = ?"
private const val ID_SELECTION = "${ContactsContract.Contacts._ID} = ?"

class ContentResolverHelper(
    context: Context
) {
    private val contextReference = WeakReference(context)
    private val selectionHelper = SelectionHelper()

    fun populateSimpleContactEntitiesInMap(
        entities: Map<String, Int>,
        block: Cursor.() -> Unit
    ) = safeResolverContext {
        val selection = selectionHelper.contactDataSelectionFromMap(entities)

        query(
            ContactsContract.Data.CONTENT_URI,
            DATA_PROJECTION,
            selection,
            entities.keys.toTypedArray(),
            DATA_SORT_ORDER
        )?.use {
            if (it.moveToFirst()) {
                do {
                    block(it)
                } while (it.moveToNext())
            }
        }
    }

    fun getSimpleContactEntitiesByIdsAndQuery(
        contactsIds: List<Long>?,
        contactQueryString: String?,
        block: Cursor.() -> Unit
    ) = safeResolverContext {
        val selection = selectionHelper.contactSelectionFromContactsIdsAndQuery(
            contactsIds,
            contactQueryString
        )
        val selectionArgs = selectionHelper.contactSelectionArgsFromContactsIdsAndQuery(
            contactsIds,
            contactQueryString
        )

        query(
            ContactsContract.Contacts.CONTENT_URI,
            CONTACT_PROJECTION,
            selection,
            selectionArgs,
            CONTACT_SORT_ORDER
        )?.use {
            if (it.moveToFirst()) {
                do {
                    block(it)
                } while (it.moveToNext())
            }
        }
    }

    fun getContactByLookup(lookup: String, block: Cursor.() -> Unit) = safeResolverContext {
        query(
            ContactsContract.Contacts.CONTENT_URI,
            CONTACT_PROJECTION,
            LOOKUP_SELECTION,
            arrayOf(lookup),
            CONTACT_SORT_ORDER
        )?.use {
            if (it.moveToFirst()) {
                block(it)
            }
        }
    }

    fun populateContactEntity(
        contactEntityFramework: ContactEntity,
        block: Cursor.() -> Unit
    ) = safeResolverContext {
        query(
            ContactsContract.Data.CONTENT_URI,
            DATA_PROJECTION,
            LOOKUP_SELECTION,
            arrayOf(contactEntityFramework.lookup),
            null
        )?.use {
            if (it.moveToFirst()) {
                do {
                    block(it)
                } while (it.moveToNext())
            }
        }
    }

    fun getContactLookupById(id: Long, block: Cursor.() -> Unit) = safeResolverContext {
        query(
            ContactsContract.Contacts.CONTENT_URI,
            CONTACT_PROJECTION,
            ID_SELECTION,
            arrayOf(id.toString()),
            CONTACT_SORT_ORDER
        )?.use {
            if (it.moveToFirst()) {
                block(it)
            }
        }
    }

    private inline fun safeResolverContext(block: ContentResolver.() -> Unit) {
        val dereferencedContext = contextReference.get() ?: throw IllegalStateException(
            "${this::class.simpleName} must have a valid context instance"
        )

        dereferencedContext.contentResolver
            ?.let(block)
    }
}
