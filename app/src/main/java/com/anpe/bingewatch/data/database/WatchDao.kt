package com.anpe.bingewatch.data.database

import androidx.room.*
import com.anpe.bingewatch.data.entity.WatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchDao {
    @Insert
    fun insertWatch(vararg entity: WatchEntity)

    @Update
    fun updateWatch(vararg entity: WatchEntity)

    @Upsert
    fun upsertWatch(vararg entity: WatchEntity)

    @Delete
    fun deleteWatch(vararg entity: WatchEntity)

    @Query("DELETE FROM watch_table")
    fun deleteAllWatch()

    @Query("DELETE FROM watch_table WHERE id = :id")
    fun deleteWatch(id: Long)

    // DESC ASC
    @Query("SELECT * FROM watch_table WHERE is_delete LIKE 0 ORDER BY change_time DESC")
    fun getAllWatchFlow(): Flow<List<WatchEntity>>

    // DESC ASC
    @Query("SELECT * FROM watch_table WHERE is_delete LIKE 0 ORDER BY change_time DESC")
    suspend fun getAllWatch(): List<WatchEntity>

    @Query("SELECT * FROM watch_table WHERE id LIKE :id")
    suspend fun findWatch(id: Long): WatchEntity

    // New
//    @Query("SELECT * FROM watch_table WHERE watch_state LIKE :pattenState ORDER BY change_time DESC")
//    fun findWatchFlow(pattenState: Int): Flow<List<WatchEntity>>

//    @Query("SELECT * FROM watch_table WHERE watch_state LIKE :pattenState AND title LIKE :pattenTitle ORDER BY change_time DESC")
//    fun findWatchFlow(pattenState: Int, pattenTitle: String): Flow<List<WatchEntity>>

    @Query("SELECT * FROM watch_table WHERE title LIKE :patten")
    fun findWatchTitleFlow(patten: String): Flow<List<WatchEntity>>

    @Query("SELECT * FROM watch_table WHERE title LIKE :patten")
    suspend fun findWatchTitle(patten: String): List<WatchEntity>
}