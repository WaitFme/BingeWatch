package com.anpe.bingewatch.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anpe.bingewatch.utils.Tools.Companion.toDateStr
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
    }
}