package com.example.clubhouse.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clubhouse.R
import com.example.clubhouse.data.SimpleContactEntity
import com.example.clubhouse.databinding.FragmentContactListBinding
import com.example.clubhouse.ui.adapters.ContactAdapter
import com.example.clubhouse.ui.adapters.Item
import com.example.clubhouse.ui.adapters.decorations.ContactListDecoration
import com.example.clubhouse.ui.adapters.decorations.ContactListDecorationProperties
import com.example.clubhouse.ui.interfaces.ContactCardClickListener
import com.example.clubhouse.ui.interfaces.ReadContactsPermissionRequester
import com.example.clubhouse.ui.viewmodels.ContactListViewModel

const val CONTACT_LIST_FRAGMENT_TAG = "fragment_contact_list"

class ContactListFragment :
    Fragment(R.layout.fragment_contact_list) {
    private var cardClickListener: ContactCardClickListener? = null
    private var viewAdapter: ContactAdapter? = null
    private var binding: FragmentContactListBinding? = null
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
        viewAdapter?.items = listOf(Item.HeaderItem).plus(list.toItems()).plus(listOf(Item.FooterItem(list.size)))
    }

    private fun List<SimpleContactEntity>.toItems() = map {
        Item.ContactItem(it)
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
            addItemDecoration(
                ContactListDecoration(
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
            )
        }
    }

    private fun updateUI() {
        viewModel.contactList.observe(viewLifecycleOwner) {
            updateContactList(it)

            binding?.contactListRefreshView?.isRefreshing = false
        }

        (activity as? ReadContactsPermissionRequester)?.run {
            requestPermission {
                viewModel.contactList.observe(viewLifecycleOwner) {
                    updateContactList(it)
                }

                viewModel.provideContactList()
            }
        }
    }

    private fun search(query: String?) {
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