package com.anpe.bingewatch.data.local.database

import androidx.room.*
import com.anpe.bingewatch.data.local.entity.WatchNewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchDao {
    @Insert
    fun insertWatch(vararg entity: WatchNewEntity)

    @Update
    fun updateWatch(vararg entity: WatchNewEntity)

    @Delete
    fun deleteWatch(vararg entity: WatchNewEntity)

    @Query("DELETE FROM watch_table")
    fun deleteAllWatch()

    // DESC ASC
    @Query("SELECT * FROM watch_table ORDER BY change_time DESC")
    fun getAllWatch(): Flow<List<WatchNewEntity>>

    // New
    @Query("SELECT * FROM watch_table WHERE watch_state LIKE :pattenState ORDER BY change_time DESC")
    fun findWatchFlow(pattenState: Int): Flow<List<WatchNewEntity>>

    @Query("SELECT * FROM watch_table WHERE watch_state LIKE :pattenState AND title LIKE :pattenTitle ORDER BY change_time DESC")
    fun findWatchFlow(pattenState: Int, pattenTitle: String): Flow<List<WatchNewEntity>>

    @Query("SELECT * FROM watch_table WHERE title LIKE :patten")
    fun findWatchTitleFlow(patten: String): Flow<List<WatchNewEntity>>
}