
package com.example.atlaspaises.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.atlaspaises.data.local.CountryEntity
import com.example.atlaspaises.ui.viewmodel.CountryViewModel

@Composable
fun MyCountriesScreen(
    viewModel: CountryViewModel,
    onPaisClick: (String) -> Unit
) {
    val misPaises by viewModel.misPaises
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val visitados by viewModel.totalVisitados
        .collectAsStateWithLifecycle(initialValue = 0)

    val total by viewModel.totalGuardados
        .collectAsStateWithLifecycle(initialValue = 0)

    var filtro by remember {
        mutableStateOf("Todos")
    }

    val opciones = listOf(
        "Todos",
        "Por visitar",
        "Visitados"
    )

    val paisesFiltrados = remember(misPaises, filtro) {
        misPaises.filter { pais ->
            when (filtro) {
                "Por visitar" ->
                    pais.estado == "Por visitar"

                "Visitados" ->
                    pais.estado == "Visitado"

                else -> true
            }
        }.sortedBy { it.nombre.lowercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Mis países",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Visitados: $visitados de $total",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Tu colección de países",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            opciones.forEach { opcion ->
                FilterChip(
                    selected = filtro == opcion,
                    onClick = { filtro = opcion },
                    label = {
                        Text(
                            opcion,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (paisesFiltrados.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (misPaises.isEmpty())
                        "Todavía no has guardado países"
                    else
                        "No hay países en esta categoría"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = paisesFiltrados,
                    key = { it.codigo }
                ) { pais ->

                    PaisGuardadoItem(
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
            }
        }
    }
}

@Composable
private fun PaisGuardadoItem(
    pais: CountryEntity,
    onClick: () -> Unit,
    onCambiarEstado: (String?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pais.bandera.startsWith("http")) {
                AsyncImage(
                    model = pais.bandera,
                    contentDescription =
                        "Bandera de ${pais.nombre}",
                    modifier = Modifier.size(70.dp, 48.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = pais.bandera,
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pais.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = pais.estado ?: "No guardado"
                )
            }
        }

        EstadoPaisMenu(
            estado = pais.estado,
            onCambiarEstado = onCambiarEstado,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 10.dp
                )
        )
    }
}
