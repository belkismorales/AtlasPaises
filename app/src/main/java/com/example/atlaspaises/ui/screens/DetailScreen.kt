
package com.example.atlaspaises.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.atlaspaises.ui.viewmodel.CountryViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DetailScreen(
    codigo: String,
    viewModel: CountryViewModel,
    onVolver: () -> Unit
) {
    val flujoPais = remember(codigo, viewModel) {
        viewModel.obtenerPais(codigo)
    }

    val pais by flujoPais.collectAsStateWithLifecycle(
        initialValue = null
    )

    val cargando by viewModel.detalleCargando
        .collectAsStateWithLifecycle()

    val error by viewModel.detalleError
        .collectAsStateWithLifecycle()

    LaunchedEffect(codigo) {
        viewModel.consultarDetalle(codigo)
    }

    val formato = remember {
        NumberFormat.getNumberInstance(
            Locale("es", "PE")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        TextButton(onClick = onVolver) {
            Text("← Volver")
        }

        Text(
            text = "Detalle del país",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        if (cargando) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error
            )

            Button(
                onClick = {
                    viewModel.consultarDetalle(codigo)
                },
                enabled = !cargando
            ) {
                Text("Reintentar")
            }
        }

        if (pais == null) {
            if (!cargando && error == null) {
                Text("No disponible")
            }
        } else {
            val actual = pais!!

            if (actual.bandera.startsWith("http")) {
                AsyncImage(
                    model = actual.bandera,
                    contentDescription =
                        "Bandera de ${actual.nombre}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = actual.bandera,
                    style = MaterialTheme.typography.displayLarge
                )
            }

            Text(
                text = actual.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = actual.nombreOficial,
                style = MaterialTheme.typography.bodyLarge
            )

            HorizontalDivider()

            DatoDetalle("Código", actual.codigo)
            DatoDetalle("Capital", actual.capital)

            DatoDetalle(
                "Región",
                traducirRegion(actual.region)
            )

            DatoDetalle(
                "Subregión",
                actual.subregion
            )

            DatoDetalle(
                "Población",
                actual.poblacion?.let {
                    formato.format(it)
                } ?: "No disponible"
            )

            DatoDetalle(
                "Área",
                actual.area?.let {
                    "${formato.format(it)} km²"
                } ?: "No disponible"
            )

            DatoDetalle("Monedas", actual.monedas)
            DatoDetalle("Idiomas", actual.idiomas)
            DatoDetalle(
                "Países limítrofes",
                actual.fronteras
            )

            HorizontalDivider()

            Text(
                text = "Mi colección",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Estado actual: ${actual.estado ?: "No guardado"}"
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.cambiarEstado(
                            codigo, "Por visitar"
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Por visitar")
                }

                Button(
                    onClick = {
                        viewModel.cambiarEstado(
                            codigo, "Visitado"
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Visitado")
                }
            }

            if (actual.estado != null) {
                OutlinedButton(
                    onClick = {
                        viewModel.cambiarEstado(codigo, null)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Quitar de Mis países")
                }
            }
        }
    }
}

@Composable
private fun DatoDetalle(
    titulo: String,
    valor: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = valor.ifBlank { "No disponible" },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
