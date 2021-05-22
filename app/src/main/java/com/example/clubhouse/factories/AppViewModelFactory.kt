package com.example.clubhouse.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class AppViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>,
        @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return creators[modelClass]?.get() as? T
            ?: throw IllegalArgumentException(
                this::class
                    .simpleName
                    .plus(
                        "wasn't able to find the requested"
                    )
                    .plus(modelClass.simpleName)
            )
    }
}
