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

package com.sky.xposed.load.util

import android.util.Log

/**
 * Created by sky on 18-1-5.
 */
object Alog {

    private val tag = "Alog"
    var debug = true

    fun i(msg: String) {
        if (debug) {
            i(tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (debug) {
            Log.i(tag, msg)
        }
    }

    fun i(tag: String, msg: String, tr: Throwable) {
        if (debug) {
            Log.i(tag, msg, tr)
        }
    }

    fun d(msg: String) {
        d(tag, msg)
    }

    fun d(tag: String, msg: String) {
        if (debug) {
            Log.d(tag, msg)
        }
    }

    fun d(tag: String, msg: String, tr: Throwable) {
        if (debug) {
            Log.d(tag, msg, tr)
        }
    }

    fun e(msg: String) {
        e(tag, msg)
    }

    fun e(msg: String, tr: Throwable) {
        e(tag, msg, tr)
    }

    fun e(tag: String, msg: String) {
        if (debug) {
            Log.e(tag, msg)
        }
    }

    fun e(tag: String, msg: String, tr: Throwable) {
        if (debug) {
            Log.e(tag, msg, tr)
        }
    }

    fun w(msg: String) {
        if (debug) {
            w(tag, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (debug) {
            Log.w(tag, msg)
        }
    }

    fun w(tag: String, msg: String, tr: Throwable) {
        if (debug) {
            Log.w(tag, msg, tr)
        }
    }
}