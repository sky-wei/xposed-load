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
import android.content.SharedPreferences

/**
 * Created by sky on 18-1-9.
 */
class VPreferences {

    private val mSharedPreferences: SharedPreferences

    private constructor(context: Context): this(context, "config")

    private constructor(context: Context, name: String) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    companion object {

        fun getInstance(context: Context, name: String): VPreferences {
            return VPreferences(context, name)
        }

        fun getInstance(context: Context): VPreferences {
            return VPreferences(context)
        }
    }

    fun getInt(key: String, defValue: Int): Int {
        return mSharedPreferences.getInt(key, defValue)
    }

    fun getLong(key: String, defValue: Long): Long {
        return mSharedPreferences.getLong(key, defValue)
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return mSharedPreferences.getBoolean(key, defValue)
    }

    fun getString(key: String, defValue: String): String {
        return mSharedPreferences.getString(key, defValue)
    }

    fun putInt(key: String, value: Int) {
        mSharedPreferences.edit().putInt(key, value).apply()
    }

    fun putLong(key: String, value: Long) {
        mSharedPreferences.edit().putLong(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        mSharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun putString(key: String, value: String) {
        mSharedPreferences.edit().putString(key, value).apply()
    }
}