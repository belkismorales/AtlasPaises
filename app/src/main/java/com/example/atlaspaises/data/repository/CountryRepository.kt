
package com.example.atlaspaises.data.repository

import com.example.atlaspaises.data.local.CountryDao
import com.example.atlaspaises.data.local.CountryEntity
import com.example.atlaspaises.data.remote.CountryDto
import com.example.atlaspaises.data.remote.CountriesResponse
import com.example.atlaspaises.data.remote.RetrofitClient
import com.google.gson.JsonElement
import kotlinx.coroutines.flow.Flow

class CountryRepository(
    private val dao: CountryDao
) {
    private val api = RetrofitClient.api

    val todosLosPaises: Flow<List<CountryEntity>> =
        dao.obtenerTodos()

    val misPaises: Flow<List<CountryEntity>> =
        dao.obtenerMisPaises()

    val totalVisitados: Flow<Int> =
        dao.contarVisitados()

    val totalGuardados: Flow<Int> =
        dao.contarGuardados()

    fun obtenerPais(codigo: String): Flow<CountryEntity?> =
        dao.obtenerPorCodigo(codigo)

    // Obtener una página de 25 países
    suspend fun cargarPaises(
        offset: Int = 0
    ): CountriesResponse {
        val respuesta = api.obtenerPaises(
            limit = 25,
            offset = offset
        )
        guardarRespuesta(respuesta)
        return respuesta
    }

    // Buscar países por nombre
    suspend fun buscarPaises(
        nombre: String,
        offset: Int = 0
    ): CountriesResponse {
        val respuesta = api.buscarPaises(
            nombre = nombre,
            limit = 25,
            offset = offset
        )
        guardarRespuesta(respuesta)
        return respuesta
    }

    // Filtrar países por región
    suspend fun filtrarPorRegion(
        region: String,
        offset: Int = 0
    ): CountriesResponse {
        val respuesta = api.filtrarPorRegion(
            region = region,
            limit = 25,
            offset = offset
        )
        guardarRespuesta(respuesta)
        return respuesta
    }

    // Consultar detalle por código
    suspend fun consultarDetalle(codigo: String) {
        val respuesta = api.obtenerDetalle(codigo)
        guardarRespuesta(respuesta)
    }

    // Guardar los datos recibidos desde Retrofit
    private suspend fun guardarRespuesta(
        respuesta: CountriesResponse
    ) {
        respuesta.data.objects.forEach { dto ->
            val codigo = dto.codes?.alpha2
                ?: return@forEach

            val anterior = dao.buscarPorCodigo(codigo)

            val entidad = dto.toEntity(
                estadoAnterior = anterior?.estado
            )

            dao.insertar(entidad)
        }
    }

    // Cambiar o quitar el estado del país
    suspend fun cambiarEstado(
        codigo: String,
        estado: String?
    ) {
        require(
            estado == null ||
                    estado == "Por visitar" ||
                    estado == "Visitado"
        )

        dao.actualizarEstado(codigo, estado)
    }
}

// Convertir CountryDto a CountryEntity
private fun CountryDto.toEntity(
    estadoAnterior: String?
): CountryEntity {

    return CountryEntity(
        codigo = codes?.alpha2.orEmpty(),
        nombre = names?.common ?: "No disponible",
        nombreOficial = names?.official ?: "No disponible",
        bandera = flag?.urlPng ?: flag?.emoji.orEmpty(),
        region = region ?: "No disponible",
        subregion = subregion ?: "No disponible",
        capital = capitals
            ?.firstOrNull()
            ?.name ?: "Sin capital",
        poblacion = population,
        area = area?.kilometers,
        monedas = convertirMonedas(currencies),
        idiomas = convertirIdiomas(languages),
        fronteras = borders
            ?.joinToString(", ")
            ?: "No disponible",
        estado = estadoAnterior
    )
}

// Convertir monedas a texto

private fun convertirMonedas(
    monedas: JsonElement?
): String {

    if (monedas == null || monedas.isJsonNull) {
        return "No disponible"
    }

    val resultado = when {
        monedas.isJsonArray -> {
            monedas.asJsonArray.mapNotNull { elemento ->
                if (!elemento.isJsonObject) {
                    null
                } else {
                    val objeto = elemento.asJsonObject

                    val nombre = objeto.get("name")
                        ?.takeUnless { it.isJsonNull }
                        ?.asString ?: "No disponible"

                    val simbolo = objeto.get("symbol")
                        ?.takeUnless { it.isJsonNull }
                        ?.asString ?: ""

                    "$nombre $simbolo".trim()
                }
            }
        }

        monedas.isJsonObject -> {
            monedas.asJsonObject.entrySet().map { entrada ->
                if (!entrada.value.isJsonObject) {
                    entrada.key
                } else {
                    val objeto = entrada.value.asJsonObject

                    val nombre = objeto.get("name")
                        ?.takeUnless { it.isJsonNull }
                        ?.asString ?: entrada.key

                    val simbolo = objeto.get("symbol")
                        ?.takeUnless { it.isJsonNull }
                        ?.asString ?: ""

                    "$nombre $simbolo".trim()
                }
            }
        }

        else -> emptyList()
    }

    return resultado.joinToString(", ")
        .ifBlank { "No disponible" }
}

// Convertir idiomas a texto
private fun convertirIdiomas(
    idiomas: JsonElement?
): String {
    if (idiomas == null || !idiomas.isJsonArray) {
        return "No disponible"
    }

    val resultado = idiomas.asJsonArray.mapNotNull { elemento ->
        if (!elemento.isJsonObject) {
            null
        } else {
            val objeto = elemento.asJsonObject
            val nombre = objeto.get("name")
                ?: objeto.get("english_name")

            nombre?.takeUnless { it.isJsonNull }?.asString
        }
    }

    return resultado.joinToString(", ")
        .ifBlank { "No disponible" }
}
