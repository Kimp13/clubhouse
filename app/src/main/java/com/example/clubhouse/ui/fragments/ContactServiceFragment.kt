package com.example.clubhouse.ui.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.clubhouse.ui.interfaces.ContactServiceOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class ContactServiceFragment(resId: Int) : Fragment(resId), CoroutineScope {
    private val job = Job()
    protected var serviceOwner: ContactServiceOwner? = null

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ContactServiceOwner) {
            serviceOwner = context
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
}