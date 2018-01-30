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

package com.sky.xposed.load.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.sky.xposed.app.BuildConfig
import com.sky.xposed.app.R
import com.sky.xposed.load.ui.base.BaseFragment
import com.sky.xposed.load.ui.util.ActivityUtil

/**
 * Created by sky on 18-1-7.
 */
class AboutFragment : BaseFragment() {

    @BindView(R.id.tv_version)
    lateinit var tvVersion: TextView

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun initView(view: View, args: Bundle?) {

        // 设置版本名称
        tvVersion.text = getString(
                R.string.version_x, BuildConfig.VERSION_NAME)
    }

    @OnClick(R.id.tv_source)
    fun onClick(view: View) {

        val uri = Uri.parse("https://github.com/jingcai-wei/xposed-load")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        ActivityUtil.startActivity(context, intent)
    }
}