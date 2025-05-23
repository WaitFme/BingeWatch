package com.anpe.bingewatch.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anpe.bingewatch.data.entity.WatchEntity
import com.anpe.bingewatch.utils.Tools.Companion.toDateStr
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.text.SimpleDateFormat
import java.util.*

class Tools {
    companion object {
        fun Long.toDateStr(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
            val date = Date(this)
            val format = SimpleDateFormat(pattern, Locale.CHINA)
            return format.format(date)
        }

        fun Long.getTime(): String {
            val dateOld = Date(this)
            val dateNow = Date(System.currentTimeMillis())
            val cOld = Calendar.getInstance()
            val cNow = Calendar.getInstance()
            cOld.time = dateOld
            val yearOld = cOld.get(Calendar.YEAR)
            cNow.time = dateNow
            val yearNow = cNow.get(Calendar.YEAR)

            val pattern = if (yearOld == yearNow) {
                "MM月dd日"
            } else {
                "yy年MM月dd日"
            }
            val format = SimpleDateFormat(pattern, Locale.CHINA)
            return format.format(dateOld)
        }

        @SuppressLint("InternalInsetResource", "DiscouragedApi")
        fun getStatusBarHeight(context: Context): Dp {
            val resId = context.resources.getIdentifier(
                "status_bar_height", "dimen", "android"
            )
            val pxHeight =  context.resources.getDimensionPixelSize(resId)
            val scale = context.resources.displayMetrics.density
            return (pxHeight / scale + 0.5f).toInt().dp
        }

        fun WatchEntity.change(
            title: String? = null,
            remarks: String? = null,
            currentEpisode: Int? = null,
            totalEpisode: Int? = null,
            watchState: Int? = null,
            createTime: Long? = null,
            changeTime: Long? = null,
            isDelete: Boolean? = null,
        ): WatchEntity {
            val entity = this
            return WatchEntity(
                id = entity.id,
                title = title?:entity.title,
                currentEpisode = currentEpisode?:entity.currentEpisode,
                totalEpisode = totalEpisode?:entity.totalEpisode,
                watchState = watchState?:entity.watchState,
                createTime = createTime?:entity.createTime,
                changeTime = changeTime?:entity.changeTime,
                remarks = remarks?:entity.remarks,
                isDelete = isDelete?:entity.isDelete
            )
        }

        fun getWatchState(ce: Int, te: Int): Int = when (ce) {
            0 -> 1
            te -> 2
            else -> 0
        }

        fun String.numberFilter(): String {
            val regex = Regex("\\d+")
            return regex.findAll(this).joinToString { it.value }
        }
    }
}