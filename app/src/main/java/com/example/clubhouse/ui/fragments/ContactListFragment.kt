package com.example.clubhouse.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.clubhouse.R
import com.example.clubhouse.data.SimpleContactEntity
import com.example.clubhouse.databinding.FragmentContactListBinding
import com.example.clubhouse.ui.interfaces.ContactCardClickListener
import com.example.clubhouse.ui.interfaces.ContactServiceConsumer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

const val CONTACT_LIST_FRAGMENT_TAG = "fragment_contact_list"

class ContactListFragment :
    ContactServiceFragment(R.layout.fragment_contact_list),
    ContactServiceConsumer {
    private var binding: FragmentContactListBinding? = null
    private var listener: ContactCardClickListener? = null
    private var contact: SimpleContactEntity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ContactCardClickListener) {
            listener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setTitle(R.string.contact_list)
        }

        binding = FragmentContactListBinding.bind(view).apply {
            contactCard.root.setOnClickListener {
                listener?.onCardClick(1)
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
        listener = null

        super.onDetach()
    }

    override fun onServiceBoundListener() {
        updateUI()
    }

    private fun updateContact(contact: SimpleContactEntity) {
        this.contact = contact

        binding?.contactCard?.run {
            contactCardName.text = contact.name
            contactCardPhone.text = contact.phoneNumber ?: getString(
                R.string.no_phone_number
            )
        }
    }

    private fun updateUI() {
        serviceOwner?.getService()?.let { service ->
            launch {
                try {
                    service.getContacts().firstOrNull()?.let {
                        updateContact(it)
                    }
                } catch (e: CancellationException) {
                    println("Interrupted")
                }
            }
        }
    }
}