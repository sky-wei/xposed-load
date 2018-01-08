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

import com.sky.android.cherry.base.BasePresenter
import com.sky.xposed.load.data.local.PluginManager
import com.sky.xposed.load.util.Alog
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by sky on 18-1-5.
 */
abstract class AbstractPresenter : BasePresenter {

    override fun resume() {

    }

    override fun pause() {

    }

    override fun destroy() {

    }

    fun <T> ioToMain(observable: Observable<T>): Observable<T> {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> onUnsafeCreate(next: () -> T): Observable<T> {

        return Observable.unsafeCreate<T> {

            try {
                it.onNext(next.invoke())
                it.onCompleted()
            } catch (tr: Throwable) {
                Alog.e(PluginManager.TAG, "处理异常", tr)
                it.onError(tr)
            }
        }
    }
}