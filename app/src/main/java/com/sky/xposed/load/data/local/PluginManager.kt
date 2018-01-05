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

package com.sky.xposed.load.data.local

import android.content.Context
import com.sky.xposed.load.data.model.PluginModel
import com.sky.xposed.load.util.Alog
import rx.Observable

/**
 * Created by sky on 18-1-5.
 */
class PluginManager private constructor() {

    private object Holder { val INSTANCE = PluginManager() }

    companion object {

        val TAG = "PluginManager"

        val INSTANCE: PluginManager by lazy { Holder.INSTANCE }
    }

    private var mInitialize = false
    private lateinit var mContext: Context

    fun initialize(context: Context) {

        if (mInitialize) return

        mContext = context
    }

    fun release() {

        if (!mInitialize) return
    }

    fun loadPlugins(): Observable<List<PluginModel>> {
        return loadPlugins{ it == mContext.packageName }
    }

    fun loadPlugins(filter: (packageName: String) -> Boolean): Observable<List<PluginModel>> {
        return onUnsafeCreate{ loadLocalPlugins(filter) }
    }

    fun loadLocalPlugins(filter: (packageName: String) -> Boolean): List<PluginModel> {

        return listOf()
    }

    fun <T> onUnsafeCreate(next: () -> T): Observable<T> {

        return Observable.unsafeCreate<T> {

            try {
                it.onNext(next.invoke())
                it.onCompleted()
            } catch (tr: Throwable) {
                Alog.e(TAG, "处理异常", tr)
                it.onError(tr)
            }
        }
    }
}