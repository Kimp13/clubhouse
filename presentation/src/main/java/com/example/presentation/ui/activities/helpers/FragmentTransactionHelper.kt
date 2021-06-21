package com.example.presentation.ui.activities.helpers

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class FragmentTransactionHelper(
    private val fragmentManager: FragmentManager,

    @IdRes
    private val containerViewId: Int
) {
    fun changeFragmentWithTagPushBackStack(fragment: Fragment, tag: String) =
        changeFragmentTemplate(fragment, tag) {
            addToBackStack(null)
        }

    fun changeFragmentWithTag(fragment: Fragment, tag: String) =
        changeFragmentTemplate(fragment, tag)

    fun popBackStack() {
        fragmentManager.popBackStack()
    }

    private fun changeFragmentTemplate(
        fragment: Fragment,
        tag: String,
        hook: (FragmentTransaction.() -> Unit)? = null
    ) {
        val transaction = fragmentManager.beginTransaction()

        with(transaction) {
            replace(containerViewId, fragment, tag)

            hook?.invoke(this)

            commit()
        }
    }
}
