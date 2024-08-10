package com.example.prinved.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prinved.data.Item
import com.example.prinved.data.ItemsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ItemEntryViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val uiState: StateFlow<ItemDetailsUiState> =
        itemsRepository.getLastItemsStream()
            .filterNotNull()
            .map {
                ItemDetailsUiState(outOfStock = it.caisse <= 0, itemDetails = it.toItemDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ItemDetailsUiState()
            )

    var itemUiState by mutableStateOf(ItemUiState())
        private set

    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }

    suspend fun saveItem() {
        if (validateInput()) {
            itemsRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }

    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            date.isNotBlank() && vente_om.isNotBlank() && vente_espece.isNotBlank()
        }
    }
}

data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false
)

data class ItemDetails(
    val id: Int = 0,
    val date: String = "",
    val vente_espece: String = "",
    val vente_om: String = "",
    val depot: String = "",
    val retrait: String = "",
    val stockage: String = "",
    val destockage: String = "",
    val depense: String = "",
    val orange_money: String = "",
    val caisse: String = "",
)

fun ItemDetails.toItem(): Item = Item(
    id = id,
    vente_espece = vente_espece.toIntOrNull() ?:0,
    date = date,
    vente_om = vente_om.toIntOrNull() ?:0,
    retrait = retrait.toIntOrNull() ?:0,
    depot = depot.toIntOrNull() ?:0,
    stockage = stockage.toIntOrNull() ?:0,
    destockage = destockage.toIntOrNull() ?:0,
    depense = depense.toIntOrNull() ?:0,
    orange_money = orange_money.toIntOrNull() ?:0,
    caisse = caisse.toIntOrNull() ?:0
)

fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    vente_espece = vente_espece.toString(),
    date = date,
    vente_om = vente_om.toString(),
    retrait = retrait.toString(),
    depot = depot.toString(),
    stockage = stockage.toString(),
    destockage = destockage.toString(),
    orange_money =orange_money.toString(),
    depense = depense.toString(),
    caisse = caisse.toString()
)
