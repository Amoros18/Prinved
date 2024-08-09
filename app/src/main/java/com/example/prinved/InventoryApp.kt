package com.example.prinved

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.prinved.R.string
import com.example.prinved.ui.navigation.InventoryNavHost
import com.example.prinved.ui.theme.InventoryTheme
import com.example.prinved.ui.theme.md_theme_light_onBackground
import com.example.prinved.ui.theme.md_theme_light_primary

@Composable
fun InventoryApp(navController: NavHostController = rememberNavController()) {
    InventoryNavHost(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                title,
                color = Color.White)
                },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = md_theme_light_primary),
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Filled.ArrowBack,
                        contentDescription = stringResource(string.back_button),
                        tint = Color.White,
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun InventoryAppPreview() {
    InventoryTheme {
        InventoryTopAppBar(
            title = "Monsieur",
            canNavigateBack = false,
            navigateUp = {},
            scrollBehavior = null,
            modifier = Modifier
        )
    }
}