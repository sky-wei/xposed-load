/*
 * Copyright (c) 2018 The sky Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sky.xposed.load.ui.helper

import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sky.android.common.util.DisplayUtil

/**
 * Created by sky on 18-1-5.
 */
class RecyclerHelper(
        private val refreshLayout: SwipeRefreshLayout,
        private val recyclerView: RecyclerView,
        private val onCallback: OnCallback
) : RecyclerView.OnScrollListener(), SwipeRefreshLayout.OnRefreshListener {

    var mLoadMore: Boolean = false

    init {
        refreshLayout.setOnRefreshListener(this)
        recyclerView.addOnScrollListener(this)
    }

    fun setLoadMore(loadMore: Boolean) {
        mLoadMore = loadMore
    }

    override fun onRefresh() {
        onCallback.onRefresh()
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)

        if (!mLoadMore) return

        if (newState == RecyclerView.SCROLL_STATE_IDLE
                && !refreshLayout.isRefreshing
                && isSlideToBottom(recyclerView)) {

            // 加载更多
            refreshLayout.isRefreshing = true
            onCallback.onLoadMore()
        }
    }

    fun isRefreshing(): Boolean {
        return refreshLayout.isRefreshing
    }

    fun forceRefreshing() {

        if (isRefreshing()) return

        // 显示加载进度
        refreshLayout.setProgressViewOffset(true, 0,
                DisplayUtil.dip2px(refreshLayout.context, 60f))
        refreshLayout.isRefreshing = true
    }

    fun cancelRefreshing() {

        if (!isRefreshing()) return

        refreshLayout.isRefreshing = false
    }

    private fun isSlideToBottom(recyclerView: RecyclerView): Boolean {
        return recyclerView.computeVerticalScrollExtent() +
                recyclerView.computeVerticalScrollOffset() >=
                recyclerView.computeVerticalScrollRange()
    }

    interface OnCallback {

        fun onRefresh()

        fun onLoadMore()
    }
}