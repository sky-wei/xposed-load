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

package com.sky.xposed.load.util;

import android.text.TextUtils;

/**
 * Created by sky on 2016/11/25.
 */

public class ConversionUtils {

    private static final String TAG = "ConversionUtils";

    public static long parseLong(String value) {
        return parseLong(value, 0L);
    }

    public static long parseLong(String value, long defaultValue) {

        long result = defaultValue;

        if (!TextUtils.isEmpty(value)) {
            try {
                result = Long.parseLong(value);
            } catch (NumberFormatException e) {
            }
        }
        return result;
    }
}