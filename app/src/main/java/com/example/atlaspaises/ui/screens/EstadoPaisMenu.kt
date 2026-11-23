
package com.example.atlaspaises.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun EstadoPaisMenu(
    estado: String?,
    onCambiarEstado: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember {
        mutableStateOf(false)
    }

    Box(modifier = modifier) {

        OutlinedButton(
            onClick = { expandido = true }
        ) {
            Text(
                text = estado ?: "Guardar país"
            )
        }

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = {
                expandido = false
            }
        ) {

            DropdownMenuItem(
                text = { Text("Por visitar") },
                onClick = {
                    onCambiarEstado("Por visitar")
                    expandido = false
                }
            )

            DropdownMenuItem(
                text = { Text("Visitado") },
                onClick = {
                    onCambiarEstado("Visitado")
                    expandido = false
                }
            )

            if (estado != null) {
                DropdownMenuItem(
                    text = { Text("Quitar de Mis países") },
                    onClick = {
                        onCambiarEstado(null)
                        expandido = false
                    }
                )
            }
        }
    }
}
