
package com.example.atlaspaises.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(

    @PrimaryKey
    val codigo: String,

    val nombre: String,
    val nombreOficial: String,
    val bandera: String,
    val region: String,
    val subregion: String,
    val capital: String,

    val poblacion: Long?,
    val area: Double?,

    val monedas: String,
    val idiomas: String,
    val fronteras: String,

    val estado: String? = null
)
