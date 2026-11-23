
package com.example.atlaspaises.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {

    @Query("SELECT * FROM countries ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE codigo = :codigo LIMIT 1")
    fun obtenerPorCodigo(codigo: String): Flow<CountryEntity?>

    @Query("SELECT * FROM countries WHERE codigo = :codigo LIMIT 1")
    suspend fun buscarPorCodigo(codigo: String): CountryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(pais: CountryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(paises: List<CountryEntity>)

    @Query("UPDATE countries SET estado = :estado WHERE codigo = :codigo")
    suspend fun actualizarEstado(
        codigo: String,
        estado: String?
    )

    @Query("SELECT * FROM countries WHERE estado IS NOT NULL ORDER BY nombre ASC")
    fun obtenerMisPaises(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE estado = :estado ORDER BY nombre ASC")
    fun filtrarPorEstado(estado: String): Flow<List<CountryEntity>>

    @Query("SELECT COUNT(*) FROM countries WHERE estado = 'Visitado'")
    fun contarVisitados(): Flow<Int>

    @Query("SELECT COUNT(*) FROM countries WHERE estado IS NOT NULL")
    fun contarGuardados(): Flow<Int>
}
