package com.anpe.bingewatch.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_table")
data class WatchNewEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "remarks")
    val remarks: String,
    @ColumnInfo(name = "current_episode")
    val currentEpisode: Int,
    @ColumnInfo(name = "total_episode")
    val totalEpisode: Int,
    @ColumnInfo(name = "watch_state")
    val watchState: Int,
    @ColumnInfo(name = "create_time")
    val createTime: Long,
    @ColumnInfo(name = "change_time")
    val changeTime: Long,
    @ColumnInfo(name = "is_delete")
    val isDelete: Boolean,
)
