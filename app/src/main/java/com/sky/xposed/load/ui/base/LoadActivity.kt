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

import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.sky.android.core.activity.BaseActivity

/**
 * Created by sky on 18-1-5.
 */
abstract class LoadActivity : BaseActivity() {

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

    fun setSupportActionBar(toolbar: Toolbar, title: Int, homeAsUp: Boolean) {
        setSupportActionBar(toolbar, getString(title), homeAsUp)
    }

    fun setSupportActionBar(toolbar: Toolbar?, title: String, homeAsUp: Boolean) {

        if (toolbar == null) return

        setSupportActionBar(toolbar)

        // 设置ActionBar
        val actionBar = supportActionBar?:return

        actionBar.title = title
        actionBar.setDisplayHomeAsUpEnabled(homeAsUp)
    }

    fun findBaseFragmentById(id: Int): LoadFragment? {

        val fragment = supportFragmentManager.findFragmentById(id)

        return if (fragment != null) fragment as LoadFragment else null
    }

    override fun cancelLoading() {
    }

    override fun showLoading() {
    }
}