
package com.example.atlaspaises.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.atlaspaises.data.local.CountryEntity
import com.example.atlaspaises.data.repository.CountryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CountryViewModel(
    private val repository: CountryRepository

) : ViewModel() {

    private val _detalleCargando = MutableStateFlow(false)
    val detalleCargando: StateFlow<Boolean> = _detalleCargando
    private val _detalleError = MutableStateFlow<String?>(null)
    val detalleError: StateFlow<String?> = _detalleError

    private val codigosVisibles =
        MutableStateFlow<Set<String>>(emptySet())

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    val misPaises = repository.misPaises

    val totalVisitados = repository.totalVisitados

    val totalGuardados = repository.totalGuardados


    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _hayMas = MutableStateFlow(true)
    val hayMas: StateFlow<Boolean> = _hayMas

    private val _regionSeleccionada = MutableStateFlow("Todas")
    val regionSeleccionada: StateFlow<String> = _regionSeleccionada

    private var busquedaActual = ""
    private var offsetActual = 0

    val paises = combine(
        repository.todosLosPaises,
        codigosVisibles
    ) { guardados, codigos ->
        guardados
            .filter { it.codigo in codigos }
            .sortedBy { it.nombre.lowercase() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList<CountryEntity>()
    )

    init {
        cargarPaises()
    }

    fun buscar(nombre: String) {
        if (_cargando.value) return

        busquedaActual = nombre.trim()
        _regionSeleccionada.value = "Todas"
        reiniciarConsulta()
    }

    fun seleccionarRegion(region: String) {
        if (_cargando.value) return

        _regionSeleccionada.value = region
        busquedaActual = ""
        reiniciarConsulta()
    }

    private fun reiniciarConsulta() {
        offsetActual = 0
        _hayMas.value = true
        _error.value = null
        codigosVisibles.value = emptySet()
        cargarPaises()
    }

    fun cargarPaises() {
        if (_cargando.value || !_hayMas.value) return

        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                val regionApi = when (_regionSeleccionada.value) {
                    "África" -> "Africa"
                    "América" -> "Americas"
                    "Asia" -> "Asia"
                    "Europa" -> "Europe"
                    "Oceanía" -> "Oceania"
                    else -> null
                }

                val respuesta = when {
                    busquedaActual.isNotBlank() ->
                        repository.buscarPaises(
                            nombre = busquedaActual,
                            offset = offsetActual
                        )

                    regionApi != null ->
                        repository.filtrarPorRegion(
                            region = regionApi,
                            offset = offsetActual
                        )

                    else ->
                        repository.cargarPaises(
                            offset = offsetActual
                        )
                }

                val nuevosCodigos = respuesta.data.objects
                    .mapNotNull { it.codes?.alpha2 }
                    .toSet()

                codigosVisibles.value =
                    codigosVisibles.value + nuevosCodigos

                offsetActual += respuesta.data.objects.size

                _hayMas.value =
                    respuesta.data.meta?.more ?: false

            } catch (e: Exception) {
                _error.value = when (e) {
                    is IOException ->
                        "Sin conexión a Internet. Revisa tu red."

                    is HttpException -> when (e.code()) {
                        401, 403 ->
                            "La clave de API no es válida."
                        429 ->
                            "Se alcanzó el límite de consultas."
                        else ->
                            "Error del servidor: ${e.code()}"
                    }

                    else ->
                        "No se pudieron cargar los países."
                }

                // Recuperar datos locales si falla la consulta.
                if (codigosVisibles.value.isEmpty()) {
                    val guardados =
                        repository.todosLosPaises.first()

                    val filtrados = guardados.filter { pais ->
                        val coincideNombre =
                            busquedaActual.isBlank() ||
                                    pais.nombre.contains(
                                        busquedaActual,
                                        ignoreCase = true
                                    )

                        val coincideRegion =
                            _regionSeleccionada.value == "Todas" ||
                                    when (_regionSeleccionada.value) {
                                        "África" -> pais.region == "Africa"
                                        "América" -> pais.region == "Americas"
                                        "Asia" -> pais.region == "Asia"
                                        "Europa" -> pais.region == "Europe"
                                        "Oceanía" -> pais.region == "Oceania"
                                        else -> true
                                    }

                        coincideNombre && coincideRegion
                    }

                    codigosVisibles.value =
                        filtrados.map { it.codigo }.toSet()
                }

            } finally {
                _cargando.value = false
            }
        }
    }


    // Observar un país guardado en Room
    fun obtenerPais(codigo: String) =
        repository.obtenerPais(codigo)

    // Consultar el detalle desde la API y actualizar Room
    fun consultarDetalle(codigo: String) {
        viewModelScope.launch {
            _detalleCargando.value = true
            _detalleError.value = null

            try {
                repository.consultarDetalle(codigo)
            } catch (e: Exception) {
                _detalleError.value = when (e) {
                    is IOException ->
                        "Sin conexión. Mostrando datos guardados."
                    is HttpException -> when (e.code()) {
                        401, 403 -> "Error de autenticación de API."
                        429 -> "Límite de consultas alcanzado."
                        else -> "Error del servidor: ${e.code()}"
                    }
                    else -> "No se pudo actualizar el detalle."
                }
            } finally {
                _detalleCargando.value = false
            }
        }
    }

    // Guardar, cambiar o quitar estado de visita
    fun cambiarEstado(codigo: String, estado: String?) {
        viewModelScope.launch {
            repository.cambiarEstado(codigo, estado)
        }
    }

    // Modificar estado desde Explorar o Mis países
    fun guardarPais(
        codigo: String,
        estado: String?
    ) {
        cambiarEstado(codigo, estado)
    }

    companion object {
        fun factory(
            repository: CountryRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>
                ): T = CountryViewModel(repository) as T
            }
    }
}
