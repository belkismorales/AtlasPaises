
package com.example.atlaspaises.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.atlaspaises.data.local.CountryEntity
import com.example.atlaspaises.ui.viewmodel.CountryViewModel

@Composable
fun ExploreScreen(
    viewModel: CountryViewModel,
    onPaisClick: (String) -> Unit
) {

    val paises by viewModel.paises.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val hayMas by viewModel.hayMas.collectAsStateWithLifecycle()
    val region by viewModel.regionSeleccionada.collectAsStateWithLifecycle()

    var textoBusqueda by remember { mutableStateOf("") }

    val regiones = listOf(
        "Todas", "África", "América",
        "Asia", "Europa", "Oceanía"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Atlas de Países",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Explora los países del mundo")

        Spacer(modifier = Modifier.height(12.dp))

        // BUSCADOR
        Row(verticalAlignment = Alignment.CenterVertically) {

            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                label = { Text("Buscar país") },
                singleLine = true,
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.buscar(textoBusqueda)
                    }
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                enabled = !cargando,
                onClick = {
                    viewModel.buscar(textoBusqueda)
                }
            ) {
                Text("Buscar")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // FILTROS POR REGIÓN
        Row(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            regiones.forEach { opcion ->

                FilterChip(
                    selected = region == opcion,
                    enabled = !cargando,
                    onClick = {
                        textoBusqueda = ""
                        viewModel.seleccionarRegion(opcion)
                    },
                    label = { Text(opcion) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error
            )

            Button(
                onClick = { viewModel.cargarPaises() },
                enabled = !cargando
            ) {
                Text("Reintentar")
            }
        }

        LazyColumn(
            verticalArrangement =
                Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            items(
                items = paises,
                key = { it.codigo }
            ) { pais ->
                TarjetaPais(
                    pais = pais,
                    onClick = {
                        onPaisClick(pais.codigo)
                    },
                    onCambiarEstado = { estado ->
                        viewModel.guardarPais(
                            pais.codigo,
                            estado
                        )
                    }
                )
            }

            if (cargando) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (paises.isEmpty() &&
                !cargando &&
                error == null
            ) {
                item {
                    Text("No se encontraron países")
                }
            }

            if (hayMas && !cargando && error == null) {
                item {
                    Button(
                        onClick = {
                            viewModel.cargarPaises()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cargar más")
                    }
                }
            }
        }
    }
}


@Composable
fun TarjetaPais(
    pais: CountryEntity,
    onClick: () -> Unit,
    onCambiarEstado: (String?) -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (pais.bandera.startsWith("http")) {
                    AsyncImage(
                        model = pais.bandera,
                        contentDescription =
                            "Bandera de ${pais.nombre}",
                        modifier = Modifier.size(
                            width = 80.dp,
                            height = 55.dp
                        ),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = pais.bandera,
                        style =
                            MaterialTheme.typography.headlineLarge
                    )
                }

                Spacer(
                    modifier = Modifier.width(16.dp)
                )

                Column {
                    Text(
                        text = pais.nombre,
                        fontWeight = FontWeight.Bold,
                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Región: ${traducirRegion(pais.region)}"
                    )

                    Text(
                        text = "Capital: ${pais.capital}"
                    )
                }
            }

            // Botón para guardar o cambiar estado
            EstadoPaisMenu(
                estado = pais.estado,
                onCambiarEstado = onCambiarEstado,
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 10.dp
                )
            )
        }
    }
}

fun traducirRegion(region: String): String =
    when (region) {
        "Africa" -> "África"
        "Americas" -> "América"
        "Asia" -> "Asia"
        "Europe" -> "Europa"
        "Oceania" -> "Oceanía"
        else -> region
    }
