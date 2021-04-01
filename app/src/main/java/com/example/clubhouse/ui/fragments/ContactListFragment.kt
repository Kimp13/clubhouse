package com.example.clubhouse.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.clubhouse.R
import com.example.clubhouse.data.ContactRepository
import com.example.clubhouse.data.SimpleContactEntity
import com.example.clubhouse.databinding.FragmentContactListBinding
import com.example.clubhouse.ui.interfaces.ContactCardClickListener
import com.example.clubhouse.ui.interfaces.ReadContactsPermissionRequester
import com.example.clubhouse.ui.viewmodels.ContactListViewModel

const val CONTACT_LIST_FRAGMENT_TAG = "fragment_contact_list"

class ContactListFragment :
    Fragment(R.layout.fragment_contact_list) {
    private var binding: FragmentContactListBinding? = null
    private var cardClickListener: ContactCardClickListener? = null
    private var contact: SimpleContactEntity? = null
    private val viewModel: ContactListViewModel by viewModels()

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

        setHasOptionsMenu(true)
        initializeRefreshView()
        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.fragment_contact_list_menu, menu)
        menu.findItem(R.id.menuRefresh)?.run {
            setOnMenuItemClickListener {
                binding?.contactListRefreshView?.isRefreshing = true
                refreshContactList()

                true
            }
        }
    }

    override fun onDestroyView() {
        binding = null

        super.onDestroyView()
    }

    override fun onDetach() {
        cardClickListener = null

        super.onDetach()
    }

    private fun updateContact(contact: SimpleContactEntity) {
        this.contact = contact

        binding?.contactCard?.run {
            println(this)
            contact.photoId?.let {
                contactCardPhoto.run {
                    setImageURI(ContactRepository.makePhotoUri(it))

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

    private fun updateContactList(list: List<SimpleContactEntity>) {
        list.firstOrNull()?.let {
            updateContact(it)
        }
    }

    private fun updateUI() {
        viewModel.contactList.observe(viewLifecycleOwner) {
            updateContactList(it)

            binding?.contactListRefreshView?.isRefreshing = false
        }

        (activity as? ReadContactsPermissionRequester)?.run {
            requestPermission {
                viewModel.search()
            }
        }
    }

    private fun refreshContactList() {
        viewModel.refreshContactList()

        binding?.contactListRefreshView?.isRefreshing
    }

    private fun initializeRefreshView() {
        binding?.contactListRefreshView?.setOnRefreshListener {
            refreshContactList()
        }
    }
}