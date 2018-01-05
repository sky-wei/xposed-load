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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import butterknife.ButterKnife
import com.sky.android.cherry.base.BaseView
import com.sky.xposed.load.ui.util.VToast

/**
 * Created by sky on 18-1-5.
 */
abstract class BaseActivity : AppCompatActivity(), BaseView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置
        setContentView(getLayoutId())
        ButterKnife.bind(this)

        // 初始化
        initView(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    open fun isLightStatusBar(): Boolean {
        return true
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initView(intent: Intent)

    fun getContext(): Context {
        return this
    }

    fun setSupportActionBar(toolbar: Toolbar, title: Int, homeAsUp: Boolean) {
        setSupportActionBar(toolbar, getString(title), homeAsUp)
    }

    fun setSupportActionBar(toolbar: Toolbar?, title: String, homeAsUp: Boolean) {

        if (toolbar == null) return

        setSupportActionBar(toolbar)

        // 设置ActionBar
        val actionBar = supportActionBar

        actionBar!!.title = title
        actionBar.setDisplayHomeAsUpEnabled(homeAsUp)
    }

    fun findBaseFragmentById(id: Int): BaseFragment? {

        val fragment = supportFragmentManager.findFragmentById(id)

        return if (fragment != null) fragment as BaseFragment else null
    }

    override fun showLoading() {
    }

    override fun cancelLoading() {
    }

    override fun showMessage(msg: String) {
        VToast.show(msg)
    }
}