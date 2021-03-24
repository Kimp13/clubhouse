package com.example.clubhouse.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.clubhouse.ui.interfaces.ContactServiceOwner
import com.example.clubhouse.ui.interfaces.ReadContactsPermissionRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class ContactFragment(resId: Int) : Fragment(resId), CoroutineScope {
    private val job = Job()
    protected var serviceOwner: ContactServiceOwner? = null
    protected var permissionGranted = false
    protected var serviceBound = false

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ContactServiceOwner) {
            serviceOwner = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serviceBound = serviceOwner?.getService() != null

        (activity as AppCompatActivity?)?.run {
            if (this is ReadContactsPermissionRequester) {
                requestPermission {
                    permissionGranted = true

                    updateUI()
                }
            }
        }
    }

    override fun onDestroy() {
        job.cancel()

        super.onDestroy()
    }

    override fun onDetach() {
        serviceOwner = null

        super.onDetach()
    }

    abstract fun updateUI()
}