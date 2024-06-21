package com.wishnewjam.home.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import com.wishnewjam.home.domain.PlayerViewModelFactory
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class GenericViewModelFactory<T1 : ViewModel> @Inject constructor(
    private val provider: Provider<T1>
) : PlayerViewModelFactory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        return provider.get() as T
    }
}