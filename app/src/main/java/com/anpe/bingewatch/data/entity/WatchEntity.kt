package com.anpe.bingewatch.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val remarks: String,
    val currentEpisode: Int,
    val allEpisode: Int,
    val watchingState: Int,
    val createTime: Long,
    val changeTime: Long,
    val isDelete: Boolean,
)