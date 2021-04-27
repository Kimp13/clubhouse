package com.example.clubhouse.ui.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.clubhouse.ContactApplication
import com.example.clubhouse.di.modules.ViewModelModule
import com.example.clubhouse.di.scopes.FragmentScope
import dagger.Subcomponent
import javax.inject.Inject

@Subcomponent(
    modules = [ViewModelModule::class]
)
@FragmentScope
interface ContactFragmentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ContactFragmentComponent
    }

    fun inject(contactFragment: ContactFragment)
}

open class ContactFragment(
    resId: Int
) : Fragment(resId) {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
        (activity?.application as? ContactApplication)?.applicationComponent
            ?.contactFragmentComponent()
            ?.create()
            ?.inject(this)

        super.onAttach(context)
    }
}