package com.anpe.bingewatch.data.database

import androidx.room.*
import com.anpe.bingewatch.data.entity.WatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchDao {
    @Upsert
    fun upsertWatch(vararg entity: WatchEntity)

    @Query("DELETE FROM watch_table")
    fun deleteAllWatch()

    @Query("SELECT * FROM watch_table ORDER BY id ASC")
    fun getAllWatchFlow(): Flow<List<WatchEntity>>

    @Query("SELECT * FROM watch_table WHERE is_delete = 0 ORDER BY title ASC")
    fun findAllWatchByTitleFlow(): Flow<List<WatchEntity>>

    @Query("SELECT * FROM watch_table WHERE is_delete = 0 ORDER BY create_time DESC")
    fun findAllWatchByCreateTimeFlow(): Flow<List<WatchEntity>>

    @Query("SELECT * FROM watch_table WHERE is_delete = 0 ORDER BY change_time DESC")
    fun findAllWatchByChangeTimeFlow(): Flow<List<WatchEntity>>

    @Query("SELECT * FROM watch_table WHERE title LIKE :patten")
    fun findWatchTitleFlow(patten: String): Flow<List<WatchEntity>>

    @Query("SELECT * FROM watch_table ORDER BY id ASC")
    suspend fun getAllWatch(): List<WatchEntity>

    @Query("SELECT * FROM watch_table WHERE id = :id ORDER BY id ASC")
    suspend fun findWatch(id: Long): WatchEntity

    @Query("SELECT * FROM watch_table WHERE title LIKE :patten")
    suspend fun findWatch(patten: String): List<WatchEntity>
}