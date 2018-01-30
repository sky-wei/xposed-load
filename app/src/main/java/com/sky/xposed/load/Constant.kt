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

package com.sky.xposed.load

/**
 * Created by sky on 18-1-5.
 */
object Constant {

    object Load {

        val PACKAGE_NAME = "com.sky.xposed.load"
    }

    object Cache {

        /** 内存缓存 */
        val MEMORY = 10 * 1024 * 1024

        /** 磁盘缓存 */
        val DISK = 500 * 1024 * 1024
    }

    object Key {

        val ARGS = "args"

        val TYPE = "type"

        val MODE = "mode"

        val ACTION = "action"

        val TITLE = "title"

        val NAME = "name"

        val PHONE = "phone"

        val ANY = "any"

        val ID = "id"

        val F_NAME = "fName"

        val SUPPORT_FRAGMENT = "supportFragment"
    }

    object Status {

        val DISABLED = 0x00

        val ENABLED = 0x01
    }

    object EventId {

        val CLICK = 0x01

        val LONG_CLICK = 0x02

        val SELECT = 0x03
    }

    object Filter {

        val USER = 0x01

        val SYSTEM = 0x02

        val ALL = 0x03
    }

    object Preference {

        val AUTO_KILL_APP = "auto_kill_app"

        val ROOT_KILL_APP = "root_kill_app"
    }
}