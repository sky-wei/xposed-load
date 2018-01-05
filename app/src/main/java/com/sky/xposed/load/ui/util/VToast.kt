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

import android.content.Context
import android.widget.Toast

/**
 * Created by sky on 18-1-5.
 */
class VToast private constructor() {

    private lateinit var mContext: Context

    fun init(context: Context) {
        mContext = context.applicationContext
    }

    fun showMessage(text: CharSequence, duration: Int) {
        val toast = Toast.makeText(mContext, text, duration)
//        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    fun showMessage(resId: Int, duration: Int) {
        showMessage(mContext.getString(resId), duration)
    }

    companion object {

        private object Holder { val INSTANCE = VToast() }

        val instance: VToast by lazy { Holder.INSTANCE }

        fun show(text: CharSequence) {
            instance.showMessage(text, Toast.LENGTH_SHORT)
        }

        fun show(text: CharSequence, duration: Int) {
            instance.showMessage(text, duration)
        }

        fun show(resId: Int) {
            instance.showMessage(resId, Toast.LENGTH_SHORT)
        }

        fun show(resId: Int, duration: Int) {
            instance.showMessage(resId, duration)
        }
    }
}