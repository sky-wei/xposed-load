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

package com.sky.xposed.load.contract

import com.sky.android.cherry.base.BasePresenter
import com.sky.android.cherry.base.BaseView
import com.sky.xposed.load.data.model.PluginModel

/**
 * Created by sky on 18-1-5.
 */
interface PluginManagerContract {

    interface View : BaseView {

        fun onLoadPlugins(models: List<PluginModel>)

        fun onLoadPluginsFailed(msg: String)
    }

    interface Presenter : BasePresenter {

        fun loadPlugins()
    }
}