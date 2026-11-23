
package com.example.atlaspaises.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CountriesApi {

    @GET("countries/v5")
    suspend fun obtenerPaises(
        @Query("limit") limit: Int = 25,
        @Query("offset") offset: Int = 0
    ): CountriesResponse

    @GET("countries/v5/name")
    suspend fun buscarPaises(
        @Query("q") nombre: String,
        @Query("limit") limit: Int = 25,
        @Query("offset") offset: Int = 0
    ): CountriesResponse

    @GET("countries/v5/region/{region}")
    suspend fun filtrarPorRegion(
        @Path("region") region: String,
        @Query("limit") limit: Int = 25,
        @Query("offset") offset: Int = 0
    ): CountriesResponse

    @GET("countries/v5/codes.alpha_2/{codigo}")
    suspend fun obtenerDetalle(
        @Path("codigo") codigo: String
    ): CountriesResponse
}
