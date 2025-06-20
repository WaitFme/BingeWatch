package com.anpe.bingewatch.data.mapper

import com.anpe.bingewatch.data.dto.conmon.WatchDto
import com.anpe.bingewatch.data.entity.WatchEntity

class WatchMapper {
    companion object {
        fun WatchDto.toEntity() = WatchEntity(
            title = title,
            remarks = remarks,
            watchState = state,
            currentEpisode = currentEpisode,
            totalEpisode = totalEpisode,
            createTime = createTime,
            changeTime = changeTime,
            isDelete = isDelete,
        )

        fun WatchEntity.toDto() = WatchDto(
            title = title,
            remarks = remarks,
            state = watchState,
            currentEpisode = currentEpisode,
            totalEpisode = totalEpisode,
            createTime = createTime,
            changeTime = changeTime,
            isDelete = isDelete,
        )
    }
}