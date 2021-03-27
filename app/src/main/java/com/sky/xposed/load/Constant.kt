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

        const val PACKAGE_NAME = "com.sky.xposed.load"
    }

    object Cache {

        /** 内存缓存 */
        const val MEMORY = 10 * 1024 * 1024

        /** 磁盘缓存 */
        const val DISK = 500 * 1024 * 1024
    }

    object Key {

        const val ARGS = "args"

        const val TYPE = "type"

        const val MODE = "mode"

        const val ACTION = "action"

        const val TITLE = "title"

        const val NAME = "name"

        const val PHONE = "phone"

        const val ANY = "any"

        const val ID = "id"

        const val F_NAME = "fName"

        const val SUPPORT_FRAGMENT = "supportFragment"
    }

    object Status {

        const val DISABLED = 0x00

        const val ENABLED = 0x01
    }

    object EventId {

        const val CLICK = 0x01

        const val LONG_CLICK = 0x02

        const val SELECT = 0x03
    }

    object Filter {

        const val USER = 0x01

        const val SYSTEM = 0x02

        const val ALL = 0x03
    }

    object Preference {

        const val AUTO_KILL_APP = "auto_kill_app"

        const val ROOT_KILL_APP = "root_kill_app"
    }
}