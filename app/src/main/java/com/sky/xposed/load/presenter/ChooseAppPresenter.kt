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

package com.sky.xposed.load.presenter

import android.text.TextUtils
import com.sky.xposed.load.Constant
import com.sky.xposed.load.contract.ChooseAppContract
import com.sky.xposed.load.data.local.PluginManager
import com.sky.xposed.load.data.model.AppModel
import io.reactivex.rxjava3.core.Observable

/**
 * Created by sky on 18-1-5.
 */
class ChooseAppPresenter(
        private val pluginManager: PluginManager,
        private val view: ChooseAppContract.View
) : AbstractPresenter(), ChooseAppContract.Presenter {

    var mFilter = Constant.Filter.USER
    var mAppList = ArrayList<AppModel>()

    override fun setFilter(filter: Int) {
        mFilter = filter
    }

    override fun getFilter(): Int {
        return mFilter
    }

    override fun loadApps() {

        ioToMain(pluginManager.loadApps(mFilter))
                .subscribe({
                    view.cancelLoading()
                    mAppList.clear()
                    mAppList.addAll(it)
                    view.onLoadApps(it)
                }, {
                    view.cancelLoading()
                    view.onLoadAppsFailed(it?.message?:"")
                })
    }

    override fun searchApp(keyword: String) {

        if (TextUtils.isEmpty(keyword)) {
            // 直接返回原结果
            view.onSearchApp(mAppList)
            return
        }

        ioToMain(searchListApp(keyword))
                .subscribe({
                    view.onSearchApp(it)
                }, {
                    view.onSearchAppFailed(it?.message?:"")
                })
    }

    private fun searchListApp(keyword: String): Observable<List<AppModel>> {
        return onUnsafeCreate{ searchListApp(mAppList, keyword) }
    }

    private fun searchListApp(list: List<AppModel>, keyword: String): List<AppModel> {

        val searchApp = ArrayList<AppModel>()

        list.forEach {

            if (it.label.contains(keyword, true)) {
                // 添加到列表中
                searchApp.add(it)
            }
        }
        return searchApp
    }
}