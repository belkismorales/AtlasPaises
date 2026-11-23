
package com.example.atlaspaises.data.remote

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class CountriesResponse(
    val data: CountriesData
)

data class CountriesData(
    val objects: List<CountryDto> = emptyList(),
    val meta: PageMeta? = null
)

data class PageMeta(
    val total: Int = 0,
    val count: Int = 0,
    val limit: Int = 25,
    val offset: Int = 0,
    val more: Boolean = false
)

data class CountryDto(
    val names: CountryNames? = null,
    val codes: CountryCodes? = null,
    val flag: CountryFlag? = null,
    val capitals: List<CountryCapital>? = null,
    val region: String? = null,
    val subregion: String? = null,
    val population: Long? = null,
    val area: CountryArea? = null,
    val currencies: JsonElement? = null,
    val languages: JsonElement? = null,
    val borders: List<String>? = null
)

data class CountryNames(
    val common: String? = null,
    val official: String? = null
)

data class CountryCodes(
    @SerializedName("alpha_2")
    val alpha2: String? = null,

    @SerializedName("alpha_3")
    val alpha3: String? = null
)

data class CountryFlag(
    val emoji: String? = null,

    @SerializedName("url_png")
    val urlPng: String? = null
)

data class CountryCapital(
    val name: String? = null
)

data class CountryArea(
    val kilometers: Double? = null
)
