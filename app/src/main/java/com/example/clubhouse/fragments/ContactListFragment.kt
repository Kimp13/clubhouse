package com.example.clubhouse.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.clubhouse.R
import com.example.clubhouse.databinding.FragmentContactListBinding
import com.example.clubhouse.interfaces.ContactCardClickListener

class ContactListFragment : Fragment(R.layout.fragment_contact_list) {
    private var binding: FragmentContactListBinding? = null
    private var listener: ContactCardClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ContactCardClickListener) {
            listener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentContactListBinding.bind(view).apply {
            contactCard.root.setOnClickListener {
                listener?.onCardClick(1)
            }
        }

        (activity as AppCompatActivity?)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setTitle(R.string.contact_list)
        }
    }

    override fun onDestroyView() {
        binding = null

        super.onDestroyView()
    }

    override fun onDetach() {
        listener = null

        super.onDetach()
    }
}