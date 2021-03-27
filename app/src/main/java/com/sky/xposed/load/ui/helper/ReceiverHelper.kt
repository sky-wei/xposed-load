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

package com.sky.xposed.load.ui.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.sky.android.common.util.Alog

/**
 * Created by sky on 18-1-9.
 */
class ReceiverHelper(
        context: Context,
        private val mCallback: ReceiverCallback?,
        private val mIntentFilter: IntentFilter
) {

    private val mContext: Context = context.applicationContext
    private var mHelperBroadcastReceiver: HelperBroadcastReceiver? = null

    constructor(context: Context, callback: ReceiverCallback, vararg actions: String) : this(context, callback, buildIntentFilter(*actions))

    fun registerReceiver() {

        if (mHelperBroadcastReceiver != null) return

        try {
            mHelperBroadcastReceiver = HelperBroadcastReceiver()
            mContext.registerReceiver(mHelperBroadcastReceiver, mIntentFilter)
        } catch (e: Exception) {
            Alog.e("Exception", e)
        }
    }

    fun unregisterReceiver() {

        if (mHelperBroadcastReceiver == null) return

        try {
            mContext.unregisterReceiver(mHelperBroadcastReceiver)
            mHelperBroadcastReceiver = null
        } catch (e: Exception) {
            Alog.e("Exception", e)
        }

    }

    private inner class HelperBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            // 直接回调出去就可以了
            mCallback?.onReceive(intent.action, intent)
        }
    }

    interface ReceiverCallback {

        fun onReceive(action: String, intent: Intent)
    }

    companion object {

        fun buildIntentFilter(vararg actions: String): IntentFilter {

            val filter = IntentFilter()

            if (actions.isEmpty()) {
                // 暂无
                return filter
            }

            for (action in actions) {
                // 添加Action
                filter.addAction(action)
            }

            return filter
        }
    }
}