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

package com.sky.xposed.load.data.db.convert

import android.text.TextUtils
import com.sky.xposed.load.util.ConversionUtils
import org.greenrobot.greendao.converter.PropertyConverter

/**
 * Created by sky on 18-1-4.
 */

class ListStringConverter : PropertyConverter<List<String>, String> {

    override fun convertToEntityProperty(databaseValue: String): List<String>? {

        if (TextUtils.isEmpty(databaseValue)) {
            return null
        }

        return databaseValue.split(",")
    }

    override fun convertToDatabaseValue(entityProperty: List<String>?): String? {

        if (entityProperty == null || entityProperty.isEmpty()) {
            return ""
        }

        val values = StringBuilder()
        val length = entityProperty.size

        for (i in 0 until length) {
            values.append(entityProperty[i])
            if (i != length - 1) values.append(",")
        }
        return values.toString()
    }
}