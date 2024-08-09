package com.example.prinved.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prinved.InventoryTopAppBar
import com.example.prinved.R
import com.example.prinved.ui.AppViewModelProvider
import com.example.prinved.ui.navigation.NavigationDestination
import com.example.prinved.ui.theme.InventoryTheme
import kotlinx.coroutines.launch

object ItemEditDestination : NavigationDestination {
    override val route = "item_edit"
    override val titleRes = R.string.edit_item_title
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ItemEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ItemEditBody(
            itemUiState = viewModel.itemUiState,
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateItem()
                    navigateBack()
                }
            },
            secondItemUiState = viewModel.secondItemUiState,
        /*    secondItemUiState = ItemDetailsUiState(
                outOfStock = true, itemDetails = ItemDetails(1, "", "0", "0")
            ), */
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
fun ItemEditBody(
    itemUiState: ItemUiState,
    secondItemUiState: ItemUiState,
    onItemValueChange: (ItemDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        ItemInputForm(
            itemDetails = itemUiState.itemDetails,
            secondItemDetais =secondItemUiState.itemDetails,
            onValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
            /*    onItemValueChange(itemUiState.itemDetails.copy(
                    orange_money = (secondItemUiState.itemDetails.orange_money.toInt() + itemUiState.itemDetails.vente_om.toInt() - itemUiState.itemDetails.depot.toInt() + itemUiState.itemDetails.retrait.toInt() + itemUiState.itemDetails.stockage.toInt() - itemUiState.itemDetails.destockage.toInt()).toString() ,
                    caisse = (secondItemUiState.itemDetails.caisse.toInt() + itemUiState.itemDetails.vente_espece.toInt() + itemUiState.itemDetails.depot.toInt() - itemUiState.itemDetails.retrait.toInt() - itemUiState.itemDetails.stockage.toInt() + itemUiState.itemDetails.destockage.toInt() - itemUiState.itemDetails.depense.toInt()).toString()))
           */     onSaveClick()
            },
            enabled = itemUiState.isEntryValid,
            //shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
        Spacer(modifier = Modifier.height(20.dp))

    }
}


@Preview(showBackground = true)
@Composable
fun ItemEditScreenPreview() {
    InventoryTheme {
        ItemEditScreen(navigateBack = {  }, onNavigateUp = {  })
    }
}
