package com.anpe.bingewatch.ui.host.manager

import androidx.annotation.StringRes
import com.anpe.bingewatch.R

sealed class PagerManager(val route: String, @StringRes val resourceId: Int, val dataIndex: Int) {
    data object WatchingPager : PagerManager("WatchingPager", R.string.watching_pager, 0)

    data object WantPager : PagerManager("WantPager", R.string.want_pager, 1)

    data object WatchedPager : PagerManager("WatchedPager", R.string.watched_pager, 2)
}