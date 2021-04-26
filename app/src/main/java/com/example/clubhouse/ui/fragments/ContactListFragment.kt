package com.example.clubhouse.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clubhouse.R
import com.example.clubhouse.data.entities.SimpleContactEntity
import com.example.clubhouse.databinding.FragmentContactListBinding
import com.example.clubhouse.ui.adapters.ContactAdapter
import com.example.clubhouse.ui.adapters.decorations.ContactListDecoration
import com.example.clubhouse.ui.adapters.decorations.ContactListDecorationProperties
import com.example.clubhouse.ui.adapters.items.ContactListItem
import com.example.clubhouse.ui.interfaces.ContactCardClickListener
import com.example.clubhouse.ui.interfaces.ReadContactsPermissionRequester
import com.example.clubhouse.ui.viewmodels.ContactListViewModel

const val CONTACT_LIST_FRAGMENT_TAG = "fragment_contact_list"

class ContactListFragment : ContactFragment(R.layout.fragment_contact_list) {
    private var cardClickListener: ContactCardClickListener? = null
    private var viewAdapter: ContactAdapter? = null
    private var binding: FragmentContactListBinding? = null
    private lateinit var recyclerViewDecoration: ContactListDecoration
    private val viewModel: ContactListViewModel by viewModels {
        viewModelFactory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ContactCardClickListener) {
            cardClickListener = context
        }

        recyclerViewDecoration = ContactListDecoration(
            ContactListDecorationProperties(
                verticalOffset = resources.getDimensionPixelSize(
                    R.dimen.cardVerticalMargin
                ),
                horizontalOffset = resources.getDimensionPixelSize(
                    R.dimen.cardHorizontalMargin
                ),
                junctionColor = ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                ),
                junctionWidth = resources.getDimensionPixelSize(
                    R.dimen.cardBorderWidth
                ).toFloat()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setTitle(R.string.contact_list)
        }

        binding = FragmentContactListBinding.bind(view)

        setHasOptionsMenu(true)
        initializeRecyclerView()
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

        (menu.findItem(R.id.contactListSearch)?.actionView
                as? SearchView)?.run {
            queryHint = getString(R.string.search_contacts)

            if (viewModel.searchQuery?.isNotBlank() == true) {
                setQuery(viewModel.searchQuery, false)
                isIconified = false
            }

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    search(newText)

                    return true
                }
            })
        }
    }

    override fun onDestroyView() {
        viewAdapter = null

        super.onDestroyView()
    }

    override fun onDetach() {
        cardClickListener = null

        super.onDetach()
    }

    private fun updateContactList(list: List<SimpleContactEntity>) {
        viewAdapter?.items = listOf(
            ContactListItem.Header
        ) + list.map {
            ContactListItem.Entity(it)
        } + listOf(
            ContactListItem.Footer(list.size)
        )
    }

    private fun initializeRecyclerView() {
        binding?.contactListRecyclerView?.run {
            viewAdapter = ContactAdapter {
                cardClickListener?.onCardClick(it.lookup)
            }

            adapter = viewAdapter
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )

            setHasFixedSize(true)
            addItemDecoration(recyclerViewDecoration)
        }
    }

    private fun updateUI() {
        viewModel.error.observe(viewLifecycleOwner) {
            viewAdapter?.items = listOf(ContactListItem.Error)
        }

        viewModel.contactList.observe(viewLifecycleOwner) {
            updateContactList(it)

            binding?.contactListRefreshView?.isRefreshing = false
        }

        (activity as? ReadContactsPermissionRequester)?.run {
            requestPermission {
                if (viewModel.contactList.value == null) {
                    viewAdapter?.items = listOf(ContactListItem.Progress)
                }

                viewModel.contactList.observe(viewLifecycleOwner) {
                    updateContactList(it)
                }

                viewModel.provideContactList()
            }
        }
    }

    private fun search(query: String?) {
        viewAdapter?.items = listOf(ContactListItem.Progress)

        viewModel.provideContactList(query)
    }

    private fun refreshContactList() {
        viewModel.refreshContactList()

        binding?.contactListRefreshView?.isRefreshing = true
    }

    private fun initializeRefreshView() {
        binding?.contactListRefreshView?.setColorSchemeResources(
            R.color.colorPrimary
        )
        binding?.contactListRefreshView?.setOnRefreshListener {
            refreshContactList()
        }
    }
}