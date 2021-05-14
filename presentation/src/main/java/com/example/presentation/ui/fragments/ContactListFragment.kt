package com.example.presentation.ui.fragments

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.entities.SimpleContactEntity
import com.example.presentation.R
import com.example.presentation.databinding.FragmentContactListBinding
import com.example.presentation.di.interfaces.AppComponentOwner
import com.example.presentation.ui.adapters.ContactAdapter
import com.example.presentation.ui.adapters.decorations.ContactListDecoration
import com.example.presentation.ui.adapters.decorations.ContactListDecorationProperties
import com.example.presentation.ui.adapters.items.ContactListItem
import com.example.presentation.ui.interfaces.ContactCardClickListener
import com.example.presentation.ui.interfaces.ReadContactsPermissionRequester
import com.example.presentation.ui.viewmodels.ContactListViewModel
import javax.inject.Inject

const val CONTACT_LIST_FRAGMENT_TAG = "fragment_contact_list"

class ContactListFragment : Fragment(R.layout.fragment_contact_list) {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var cardClickListener: ContactCardClickListener? = null
    private var viewAdapter: ContactAdapter? = null
    private var binding: FragmentContactListBinding? = null
    private lateinit var recyclerViewDecoration: ContactListDecoration
    private val viewModel: ContactListViewModel by viewModels {
        viewModelFactory
    }

    override fun onAttach(context: Context) {
        injectDependencies()

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
                binding?.refreshView?.isRefreshing = true
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

    private fun injectDependencies() {
        (activity?.application as? AppComponentOwner)?.applicationComponent
            ?.contactListFragmentComponent()
            ?.create()
            ?.inject(this)
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
        binding?.recyclerView?.run {
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

            binding?.refreshView?.isRefreshing = false
        }

        (activity as? ReadContactsPermissionRequester)?.run {
            requestContactPermission {
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

        binding?.refreshView?.isRefreshing = true
    }

    private fun initializeRefreshView() {
        binding?.refreshView?.setColorSchemeResources(
            R.color.colorPrimary
        )
        binding?.refreshView?.setOnRefreshListener {
            refreshContactList()
        }
    }
}