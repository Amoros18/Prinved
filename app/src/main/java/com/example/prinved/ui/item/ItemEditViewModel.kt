package com.example.prinved.ui.item

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prinved.data.ItemsRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ItemEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    var secondItemUiState by mutableStateOf(ItemUiState())
    var lastItemUiState by mutableStateOf(ItemUiState())

    var id : Int = 0
    var item1 by mutableStateOf(ItemUiState())
    var ajoutOM : Int = 0
    var ajoutCaisse : Int = 0


    var itemUiState by mutableStateOf(ItemUiState())
        private set

    private val itemId: Int = checkNotNull(savedStateHandle[ItemEditDestination.itemIdArg])

    init {
        viewModelScope.launch {
            itemUiState = itemsRepository.getItemStream(itemId)
                .filterNotNull()
                .first()
                .toItemUiState(true)
            secondItemUiState = itemsRepository.getItemStream(itemId - 1)
                .filterNotNull()
                .first()
                .toItemUiState(true)
            lastItemUiState = itemsRepository.getLastItemsStream()
                .filterNotNull()
                .first()
                .toItemUiState(true)

        }
    }

    suspend fun updateItem() {
        ajoutOM = itemUiState.itemDetails.orange_money.toInt() - ( secondItemUiState.itemDetails.orange_money.toInt() + itemUiState.itemDetails.vente_om.toInt() - itemUiState.itemDetails.depot.toInt() + itemUiState.itemDetails.retrait.toInt() + itemUiState.itemDetails.stockage.toInt() - itemUiState.itemDetails.destockage.toInt())
        ajoutCaisse =  itemUiState.itemDetails.caisse.toInt() - (secondItemUiState.itemDetails.caisse.toInt() + itemUiState.itemDetails.vente_espece.toInt() + itemUiState.itemDetails.depot.toInt() - itemUiState.itemDetails.retrait.toInt() - itemUiState.itemDetails.stockage.toInt() + itemUiState.itemDetails.destockage.toInt() - itemUiState.itemDetails.depense.toInt())
        updateUiState(itemUiState.itemDetails.copy(
            orange_money = (secondItemUiState.itemDetails.orange_money.toInt() + itemUiState.itemDetails.vente_om.toInt() - itemUiState.itemDetails.depot.toInt() + itemUiState.itemDetails.retrait.toInt() + itemUiState.itemDetails.stockage.toInt() - itemUiState.itemDetails.destockage.toInt()).toString() ,
            caisse = (secondItemUiState.itemDetails.caisse.toInt() + itemUiState.itemDetails.vente_espece.toInt() + itemUiState.itemDetails.depot.toInt() - itemUiState.itemDetails.retrait.toInt() - itemUiState.itemDetails.stockage.toInt() + itemUiState.itemDetails.destockage.toInt() - itemUiState.itemDetails.depense.toInt()).toString()))
        if (validateInput(itemUiState.itemDetails)) {
            itemsRepository.updateItem(itemUiState.itemDetails.toItem())
            id = itemId
            while (id != lastItemUiState.itemDetails.id + 1){

                item1 = itemsRepository.getItemStream(id)
                    .filterNotNull()
                    .first()
                    .toItemUiState(true)
                updateOneUiSte(item1.itemDetails.copy(
                    orange_money = (item1.itemDetails.orange_money.toInt() - ajoutOM).toString(),
                    caisse = (item1.itemDetails.caisse.toInt() - ajoutCaisse).toString()
                ))
                itemsRepository.updateItem(item1.itemDetails.toItem())
                id += 1
            }

            //itemsRepository.updateItem(secondItemUiState.itemDetails.toItem())
        }
    }

    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }
    fun updateSecondUiSte(itemDetails: ItemDetails) {
        secondItemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }
    fun updateOneUiSte(itemDetails: ItemDetails) {
        item1 =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }
    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            date.isNotBlank() && vente_om.isNotBlank() && vente_espece.isNotBlank()
                    && depot.isNotBlank() && retrait.isNotBlank() && stockage.isNotBlank()
                    && destockage.isNotBlank() && depense.isNotBlank()
        }
    }
}
