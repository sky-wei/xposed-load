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

import androidx.lifecycle.LifecycleOwner
import com.sky.android.common.util.Alog
import com.sky.android.core.interfaces.IBasePresenter
import com.sky.xposed.load.data.local.PluginManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Created by sky on 18-1-5.
 */
abstract class AbstractPresenter : IBasePresenter {

    override fun onCreate(owner: LifecycleOwner) {
    }

    override fun onDestroy(owner: LifecycleOwner) {
    }

    override fun onPause(owner: LifecycleOwner) {
    }

    override fun onResume(owner: LifecycleOwner) {
    }

    override fun onStart(owner: LifecycleOwner) {
    }

    override fun onStop(owner: LifecycleOwner) {
    }

    fun <T> ioToMain(observable: Observable<T>): Observable<T> {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> onUnsafeCreate(next: () -> T): Observable<T> {

        return Observable.unsafeCreate {

            try {
                it.onNext(next.invoke())
                it.onComplete()
            } catch (tr: Throwable) {
                Alog.e(PluginManager.TAG, "处理异常", tr)
                it.onError(tr)
            }
        }
    }
}