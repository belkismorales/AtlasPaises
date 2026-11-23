
package com.example.atlaspaises

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.atlaspaises.data.local.AppDatabase
import com.example.atlaspaises.data.repository.CountryRepository
import com.example.atlaspaises.ui.screens.ExploreScreen
import com.example.atlaspaises.ui.screens.DetailScreen
import com.example.atlaspaises.ui.screens.MyCountriesScreen
import com.example.atlaspaises.ui.viewmodel.CountryViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                val database = remember {
                    AppDatabase.getDatabase(applicationContext)
                }

                val repository = remember {
                    CountryRepository(database.countryDao())
                }

                val countryViewModel: CountryViewModel =
                    viewModel(
                        factory = CountryViewModel.factory(
                            repository
                        )
                    )

                var pantallaActual by rememberSaveable {
                    mutableStateOf("Explorar")
                }

                var codigoSeleccionado by rememberSaveable {
                    mutableStateOf<String?>(null)
                }

                BackHandler(
                    enabled = codigoSeleccionado != null
                ) {
                    codigoSeleccionado = null
                }

                Scaffold(
                    bottomBar = {
                        if (codigoSeleccionado == null) {
                            NavigationBar {

                                NavigationBarItem(
                                    selected =
                                        pantallaActual == "Explorar",
                                    onClick = {
                                        pantallaActual = "Explorar"
                                    },
                                    icon = {
                                        Icon(
                                            Icons.Default.Home,
                                            contentDescription = null
                                        )
                                    },
                                    label = {
                                        Text("Explorar")
                                    }
                                )

                                NavigationBarItem(
                                    selected =
                                        pantallaActual == "Mis países",
                                    onClick = {
                                        pantallaActual = "Mis países"
                                    },
                                    icon = {
                                        Icon(
                                            Icons.Default.Favorite,
                                            contentDescription = null
                                        )
                                    },
                                    label = {
                                        Text("Mis países")
                                    }
                                )
                            }
                        }
                    }
                ) { paddingValues ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        when {
                            codigoSeleccionado != null -> {
                                DetailScreen(
                                    codigo = codigoSeleccionado!!,
                                    viewModel = countryViewModel,
                                    onVolver = {
                                        codigoSeleccionado = null
                                    }
                                )
                            }

                            pantallaActual == "Explorar" -> {
                                ExploreScreen(
                                    viewModel = countryViewModel,
                                    onPaisClick = { codigo ->
                                        codigoSeleccionado = codigo
                                    }
                                )
                            }

                            else -> {
                                MyCountriesScreen(
                                    viewModel = countryViewModel,
                                    onPaisClick = { codigo ->
                                        codigoSeleccionado = codigo
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
