package com.example.prinved.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.prinved.InventoryApplication
import com.example.prinved.ui.home.HomeViewModel
import com.example.prinved.ui.item.ItemDetailsViewModel
import com.example.prinved.ui.item.ItemEditViewModel
import com.example.prinved.ui.item.ItemEntryViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ItemEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }
        initializer {
            ItemEntryViewModel(inventoryApplication().container.itemsRepository)
        }

        initializer {
            ItemDetailsViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            HomeViewModel(inventoryApplication().container.itemsRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.inventoryApplication(): InventoryApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication)
