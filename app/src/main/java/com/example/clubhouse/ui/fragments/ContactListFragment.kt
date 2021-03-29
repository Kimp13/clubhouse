package com.example.clubhouse.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.clubhouse.R
import com.example.clubhouse.data.DataSource
import com.example.clubhouse.data.SimpleContactEntity
import com.example.clubhouse.databinding.FragmentContactListBinding
import com.example.clubhouse.ui.interfaces.ContactCardClickListener
import com.example.clubhouse.ui.interfaces.ContactServiceConsumer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

const val CONTACT_LIST_FRAGMENT_TAG = "fragment_contact_list"

class ContactListFragment :
    ContactFragment(R.layout.fragment_contact_list),
    ContactServiceConsumer {
    private var binding: FragmentContactListBinding? = null
    private var cardClickListener: ContactCardClickListener? = null
    private var contact: SimpleContactEntity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ContactCardClickListener) {
            cardClickListener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setTitle(R.string.contact_list)
        }

        binding = FragmentContactListBinding.bind(view).apply {
            contactCard.root.setOnClickListener {
                contact?.lookup?.let { lookup ->
                    cardClickListener?.onCardClick(lookup)
                }
            }
        }

        savedInstanceState?.getParcelable<SimpleContactEntity>(CONTACT_ENTITY_KEY)?.let {
            updateContact(it)
        } ?: updateUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(CONTACT_ENTITY_KEY, contact)

        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        binding = null

        super.onDestroyView()
    }

    override fun onDetach() {
        cardClickListener = null

        super.onDetach()
    }

    override fun onServiceBoundListener() {
        serviceBound = true

        updateUI()
    }

    private fun updateContact(contact: SimpleContactEntity) {
        this.contact = contact

        binding?.contactCard?.run {
            contact.photoId?.let {
                contactCardPhoto.run {
                    setImageURI(DataSource.makePhotoUri(it))

                    imageTintList = null
                }
            }

            contactCardName.text = contact.name ?: getString(
                R.string.no_name
            )
            contactCardPhone.text = contact.phoneNumber ?: getString(
                R.string.no_phone_number
            )
        }
    }

    override fun updateUI() {
        if (serviceBound && permissionGranted) {
            serviceOwner?.getService()?.let { service ->
                launch {
                    try {
                        service.getSimpleContacts().firstOrNull()?.let {
                            updateContact(it)
                        }
                    } catch (e: CancellationException) {
                        println("Interrupted")
                    }
                }
            }
        }
    }
}