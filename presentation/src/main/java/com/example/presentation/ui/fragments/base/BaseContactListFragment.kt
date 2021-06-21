package com.example.presentation.ui.fragments.base

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.ActionBar
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
import com.example.presentation.ui.interfaces.FragmentStackGateway
import com.example.presentation.ui.interfaces.FragmentStackGatewayOwner
import com.example.presentation.ui.interfaces.PermissionGateway
import com.example.presentation.ui.interfaces.PermissionGatewayOwner
import com.example.presentation.ui.viewmodels.ContactListViewModel
import javax.inject.Inject

abstract class BaseContactListFragment : Fragment(
    R.layout.fragment_contact_list
) {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected var viewAdapter: ContactAdapter? = null
    protected var stackGateway: FragmentStackGateway? = null

    private var permissionGateway: PermissionGateway? = null
    private var binding: FragmentContactListBinding? = null
    private lateinit var recyclerViewDecoration: ContactListDecoration
    private val viewModel: ContactListViewModel by viewModels {
        viewModelFactory
    }

    protected abstract val excludedContactId: Long?

    override fun onAttach(context: Context) {
        (activity?.application as? AppComponentOwner)?.applicationComponent
            ?.contactListFragmentComponent()
            ?.create()
            ?.inject(this)

        super.onAttach(context)

        if (context is FragmentStackGatewayOwner) {
            stackGateway = context.stackGateway
        }

        if (context is PermissionGatewayOwner) {
            permissionGateway = context.permissionGateway
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

        val appCompatActivity = activity as? AppCompatActivity
        appCompatActivity?.supportActionBar?.let {
            updateActionBar(it)
        }

        binding = FragmentContactListBinding.bind(view)

        setHasOptionsMenu(true)
        initializeRecyclerView()
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

        val searchView = menu.findItem(R.id.contactListSearch)?.actionView as? SearchView
        searchView?.run {
            queryHint = getString(R.string.search_contacts)

            if (viewModel.searchQuery?.isNotBlank() == true) {
                setQuery(viewModel.searchQuery, false)
                isIconified = false
            }

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewAdapter?.items = listOf(ContactListItem.Progress)

                    viewModel.provideContactList(newText, excludedContactId)

                    return true
                }
            })
        }
    }

    protected abstract fun updateActionBar(actionBar: ActionBar)

    protected abstract fun updateContactList(list: List<SimpleContactEntity>)

    protected abstract fun makeContactAdapter(): ContactAdapter

    protected fun isSearchQueryEmpty(): Boolean {
        return viewModel.searchQuery?.isEmpty() != false
    }

    private fun initializeRecyclerView() {
        binding?.recyclerView?.run {
            viewAdapter = makeContactAdapter()
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

        binding?.refreshView?.setColorSchemeResources(
            R.color.colorPrimary
        )
        binding?.refreshView?.setOnRefreshListener {
            refreshContactList()
        }

        permissionGateway?.requestContactPermission {
            if (viewModel.contactList.value == null) {
                viewAdapter?.items = listOf(ContactListItem.Progress)
            }

            viewModel.contactList.observe(viewLifecycleOwner) {
                updateContactList(it)

                binding?.refreshView?.isRefreshing = false
            }

            viewModel.provideContactList(
                excludedContactId = excludedContactId
            )
        }
    }

    private fun refreshContactList() {
        viewModel.refreshContactList(excludedContactId = excludedContactId)

        binding?.refreshView?.isRefreshing = true
    }
}
