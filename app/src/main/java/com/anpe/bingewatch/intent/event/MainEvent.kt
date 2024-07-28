package com.anpe.bingewatch.intent.event

sealed class MainEvent {
    data class GetIndex(val type: Int): MainEvent()
}