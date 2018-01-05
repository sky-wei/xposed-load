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

package com.sky.xposed.load.ui.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Toast
import butterknife.ButterKnife
import com.sky.android.cherry.base.BaseView

/**
 * Created by sky on 18-1-5.
 */
abstract class BaseFragment : Fragment(), BaseView {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = createView(inflater, container)
        ButterKnife.bind(this, view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化View
        initView(view, arguments)
    }

    protected abstract fun createView(inflater: LayoutInflater, container: ViewGroup?): View

    protected abstract fun initView(view: View, args: Bundle?)

    override fun showLoading() {
    }

    override fun cancelLoading() {
    }

    override fun showMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    open fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    open fun onTakeOverOptionsItem(item: MenuItem): Boolean {
        return false
    }
}