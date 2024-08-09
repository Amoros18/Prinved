package com.example.prinved.ui.item

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prinved.InventoryTopAppBar
import com.example.prinved.R
import com.example.prinved.data.Item
import com.example.prinved.ui.AppViewModelProvider
import com.example.prinved.ui.navigation.NavigationDestination
import com.example.prinved.ui.theme.InventoryTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Currency
import java.util.Locale

object ItemEntryDestination : NavigationDestination {
    override val route = "item_entry"
    override val titleRes = R.string.item_entry_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ItemEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ItemEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        ItemEntryBody(
            itemUiState = viewModel.itemUiState,
            secondItemUiState = uiState.value,
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveItem()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .background(Color.White)
        )
    }
}

@Composable
fun ItemEntryBody(
    itemUiState: ItemUiState,
    secondItemUiState: ItemDetailsUiState,
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
                onItemValueChange(itemUiState.itemDetails.copy(
                    orange_money = ((secondItemUiState.itemDetails.orange_money.toIntOrNull() ?:0) + (itemUiState.itemDetails.vente_om.toIntOrNull() ?:0) -(itemUiState.itemDetails.depot.toIntOrNull() ?:0) + (itemUiState.itemDetails.retrait.toIntOrNull() ?:0) + (itemUiState.itemDetails.stockage.toIntOrNull() ?:0)- (itemUiState.itemDetails.destockage.toIntOrNull() ?:0)).toString() ,
                    caisse = ((secondItemUiState.itemDetails.caisse.toIntOrNull() ?:0) + (itemUiState.itemDetails.vente_espece.toIntOrNull() ?:0) + (itemUiState.itemDetails.depot.toIntOrNull() ?:0) - (itemUiState.itemDetails.retrait.toIntOrNull() ?:0) - (itemUiState.itemDetails.stockage.toIntOrNull() ?:0) + (itemUiState.itemDetails.destockage.toIntOrNull() ?:0) - (itemUiState.itemDetails.depense.toIntOrNull() ?:0)).toString()))
                onSaveClick()
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

@Composable
fun ItemInputForm(
    itemDetails: ItemDetails,
    secondItemDetais: ItemDetails,
    modifier: Modifier = Modifier,
    onValueChange: (ItemDetails) -> Unit = {},
    enabled: Boolean = true
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var selectedDateText by remember { mutableStateOf("") }

    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            selectedDateText = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
            onValueChange(itemDetails.copy(date = selectedDateText))
        }, year, month, dayOfMonth
    )
    if(selectedDateText ==""){
        selectedDateText = "${itemDetails.date}"
    }
    Column(
        modifier = modifier,
        //verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = itemDetails.date,
            onValueChange = { onValueChange(itemDetails.copy(date = selectedDateText)) },
            label = { Text(stringResource(R.string.date)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    datePicker.show()
                    onValueChange(itemDetails.copy(date = selectedDateText))
                }
            ,
            enabled = false,
            singleLine = true,
            //shape = RectangleShape
        )
        OutlinedTextField(
            value = itemDetails.vente_espece,
            onValueChange = { onValueChange(itemDetails.copy(vente_espece = it,date = selectedDateText)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            label = { Text(stringResource(R.string.venteEspece)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = itemDetails.vente_om,
            onValueChange = { onValueChange(itemDetails.copy(vente_om = it,date = selectedDateText)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.venteOm)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = itemDetails.depot,
            onValueChange = { onValueChange(itemDetails.copy(depot = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.depot)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = itemDetails.retrait,
            onValueChange = { onValueChange(itemDetails.copy(retrait = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.retrait)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = itemDetails.stockage,
            onValueChange = { onValueChange(itemDetails.copy(stockage = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.stockage)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = itemDetails.destockage,
            onValueChange = { onValueChange(itemDetails.copy(destockage = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.destockage)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = itemDetails.depense,
            onValueChange = {
                onValueChange(itemDetails.copy(depense = it))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.depense)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemEntryScreenPreview() {
    InventoryTheme {
        ItemEntryBody(
            itemUiState = ItemUiState(
                ItemDetails(
                    id=1,
                    date="date 1",
                    vente_espece =  "9000",
                    "2000", "4000","2023","700","900","830","87","98"
                )
        ),
            secondItemUiState = ItemDetailsUiState(
                outOfStock = true, itemDetails = ItemDetails(1, "Pen", "$100", "10")
            ),
            onItemValueChange = {}, onSaveClick = {})
    }
}
