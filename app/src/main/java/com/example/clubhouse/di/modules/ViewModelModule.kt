package com.example.clubhouse.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clubhouse.di.scopes.FragmentScope
import com.example.clubhouse.ui.factories.AppViewModelFactory
import com.example.clubhouse.ui.viewmodels.ContactDetailsViewModel
import com.example.clubhouse.ui.viewmodels.ContactListViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {
    @Binds
    @FragmentScope
    abstract fun bindViewModelFactory(
        viewModelFactory: AppViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ContactListViewModel::class)
    abstract fun bindContactListViewModel(
        viewModel: ContactListViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactDetailsViewModel::class)
    abstract fun bindContactDetailsViewModel(
        viewModel: ContactDetailsViewModel
    ): ViewModel
}