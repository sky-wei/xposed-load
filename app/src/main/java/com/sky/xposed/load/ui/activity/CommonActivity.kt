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

package com.sky.xposed.load.ui.activity

import android.content.Intent
import android.view.KeyEvent
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.hi.dhl.binding.viewbind
import com.sky.xposed.app.R
import com.sky.xposed.app.databinding.ActivityCommonBinding
import com.sky.xposed.load.Constant
import com.sky.xposed.load.ui.base.LoadActivity
import com.sky.xposed.load.ui.base.LoadFragment

/**
 * Created by sky on 18-1-5.
 */
class CommonActivity : LoadActivity() {

    private var mSupportFragment = true
    private var mFragment: LoadFragment? = null

    private val binding: ActivityCommonBinding by viewbind()

    override val layoutId: Int
        get() = R.layout.activity_common

    override fun initView(intent: Intent) {

        val title = intent.getStringExtra(Constant.Key.TITLE)
        val fName = intent.getStringExtra(Constant.Key.F_NAME)
        val args = intent.getBundleExtra(Constant.Key.ARGS)
        mSupportFragment = intent.getBooleanExtra(
                Constant.Key.SUPPORT_FRAGMENT, true)

        // 设置ActionBar
        setSupportActionBar(
                binding.appbarLayout.toolbar, title, true
        )

        if (mSupportFragment) {
            // SupportFragment
            val fragmentManager = supportFragmentManager
            val fragment = Fragment.instantiate(this, fName, args)
            fragmentManager.beginTransaction().replace(R.id.fl_frame, fragment).commit()
            mFragment = fragment as? LoadFragment
            return
        }

        // Fragment
        val fragmentManager = fragmentManager
        val fragment = android.app.Fragment.instantiate(this, fName, args)
        fragmentManager.beginTransaction().replace(R.id.fl_frame, fragment).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mFragment?.onTakeOverOptionsItem(item) == true) {
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFragment?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (mFragment != null
                && mFragment!!.onKeyDown(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}