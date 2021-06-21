package com.example.presentation.data.repositories

import android.content.Context
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.example.domain.entities.ContactEntity
import com.example.domain.entities.SimpleContactEntity
import com.example.domain.repositories.interfaces.ContactRepository
import com.example.presentation.data.repositories.helpers.ContentResolverHelper
import com.example.presentation.data.repositories.helpers.populators.ContactPopulationConductor
import com.example.presentation.data.repositories.helpers.populators.DATA_LOOKUP_INDEX
import com.example.presentation.data.repositories.helpers.populators.SimpleContactPopulationConductor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

private const val CONTACT_ID_INDEX = 0
private const val CONTACT_LOOKUP_INDEX = 1
private const val CONTACT_NAME_INDEX = 2

class ContactProviderRepository(
    context: Context
) : ContactRepository {
    private val resolverHelper = ContentResolverHelper(context)
    private val contactPopulationConductor = ContactPopulationConductor()
    private val simpleContactPopulationConductor = SimpleContactPopulationConductor()

    override suspend fun getSimpleContacts(
        query: String?,
        contactsIds: List<Long>?
    ) = withContext(Dispatchers.IO) {
        val contacts = getContactListFramework(contactsIds, query)
        val lookupToIndex = contacts.withIndex()
            .associate { it.value.lookup to it.index }

        resolverHelper.populateSimpleContactEntitiesInMap(lookupToIndex) {
            getStringOrNull(DATA_LOOKUP_INDEX)?.let { key ->
                lookupToIndex[key]
            }
                ?.let { i ->
                    contacts[i] = simpleContactPopulationConductor.populatorFromCursor(this)
                        .populate(contacts[i], this)
                }
        }

        contacts
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
            getContactId(lookup)?.let { id ->
                populateContactEntity(ContactEntity(id, lookup))
            }
        }

    override suspend fun findContactById(id: Long) =
        withContext(Dispatchers.IO) {
            getContactLookup(id)?.let { lookup ->
                populateContactEntity(ContactEntity(id, lookup))
            }
        }

    private fun getContactListFramework(
        contactsIds: List<Long>?,
        query: String?
    ): MutableList<SimpleContactEntity> {
        val contactList = mutableListOf<SimpleContactEntity>()

        resolverHelper.getSimpleContactEntitiesByIdsAndQuery(contactsIds, query) {
            contactList.add(
                SimpleContactEntity(
                    getLong(CONTACT_ID_INDEX),
                    getString(CONTACT_LOOKUP_INDEX),
                    getString(CONTACT_NAME_INDEX)
                )
            )
        }

        return contactList
    }

    private fun getContactId(lookup: String): Long? {
        var id: Long? = null

        resolverHelper.getContactByLookup(lookup) {
            getLongOrNull(CONTACT_ID_INDEX)?.let { newId ->
                id = newId
            }
        }

        return id
    }

    private fun getContactLookup(id: Long): String? {
        var lookup: String? = null

        resolverHelper.getContactLookupById(id) {
            getStringOrNull(CONTACT_LOOKUP_INDEX)?.let { newLookup ->
                lookup = newLookup
            }
        }

        return lookup
    }

    private fun populateContactEntity(contactEntityFramework: ContactEntity): ContactEntity {
        var result = contactEntityFramework

        resolverHelper.populateContactEntity(contactEntityFramework) {
            result = contactPopulationConductor.populatorFromCursor(this)
                .populate(result, this)
        }

        return result
    }
}
