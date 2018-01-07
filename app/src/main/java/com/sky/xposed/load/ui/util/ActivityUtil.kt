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

package com.sky.xposed.load.ui.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.sky.xposed.load.Constant
import com.sky.xposed.load.ui.activity.CommonActivity
import com.sky.xposed.load.ui.base.BaseFragment
import com.sky.xposed.load.util.Alog

/**
 * Created by sky on 18-1-7.
 */
object ActivityUtil {

    fun startActivity(context: Context, tClass: Class<*>): Boolean {
        return startActivity(context, Intent(context, tClass))
    }

    fun startActivity(context: Context, intent: Intent): Boolean {

        try {
            // 获取目标包名
            val packageName = intent.`package`

            // 设置启动参数
            if (!TextUtils.isEmpty(packageName)
                    && !TextUtils.equals(packageName, context.packageName)) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            // 启动Activity
            context.startActivity(intent)
            return true
        } catch (e: Exception) {
            Alog.e("启动Activity异常", e)
        }
        return false
    }

    fun startActivityForResult(activity: Activity, tClass: Class<*>, requestCode: Int): Boolean {
        return startActivityForResult(activity, Intent(activity, tClass), requestCode)
    }

    fun startActivityForResult(activity: Activity, intent: Intent, requestCode: Int): Boolean {

        try {
            // 启动Activity
            activity.startActivityForResult(intent, requestCode)
            return true
        } catch (e: Exception) {
            Alog.e("启动Activity异常", e)
        }
        return false
    }

    fun startCommonActivity(context: Context, title: String,
                            fClass: Class<out BaseFragment>): Boolean {
        return startCommonActivity(context, title, fClass.name, true, null)
    }

    fun startCommonActivity(context: Context, title: String,
                            fClass: Class<out BaseFragment>, args: Bundle?): Boolean {
        return startCommonActivity(context, title, fClass.name, true, args)
    }

    fun startCommonActivity(context: Context, title: String,
                            fName: String, supportFragment: Boolean, args: Bundle?): Boolean {

        val intent = Intent(context, CommonActivity::class.java).apply {
            putExtra(Constant.Key.TITLE, title)
            putExtra(Constant.Key.F_NAME, fName)
            putExtra(Constant.Key.SUPPORT_FRAGMENT, supportFragment)
            if (args != null) putExtra(Constant.Key.ARGS, args)
        }

        return startActivity(context, intent)
    }

    fun startCommonActivityForResult(activity: Activity, title: String,
                                     fClass: Class<out BaseFragment>, requestCode: Int): Boolean {
        return startCommonActivityForResult(activity, title, fClass.name, true, null, requestCode)
    }

    fun startCommonActivityForResult(activity: Activity, title: String,
                                     fClass: Class<out BaseFragment>, args: Bundle?, requestCode: Int): Boolean {
        return startCommonActivityForResult(activity, title, fClass.name, true, args, requestCode)
    }

    fun startCommonActivityForResult(activity: Activity, title: String,
                                     fName: String, supportFragment: Boolean, args: Bundle?, requestCode: Int): Boolean {

        val intent = Intent(activity, CommonActivity::class.java).apply {
            putExtra(Constant.Key.TITLE, title)
            putExtra(Constant.Key.F_NAME, fName)
            putExtra(Constant.Key.SUPPORT_FRAGMENT, supportFragment)
            if (args != null) putExtra(Constant.Key.ARGS, args)
        }

        return startActivityForResult(activity, intent, requestCode)
    }
}