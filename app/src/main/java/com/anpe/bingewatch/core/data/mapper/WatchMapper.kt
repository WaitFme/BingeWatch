package com.anpe.bingewatch.core.data.mapper

import com.anpe.bingewatch.core.data.dto.conmon.WatchDto
import com.anpe.bingewatch.core.data.entity.WatchEntity

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