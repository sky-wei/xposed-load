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

package com.sky.xposed.load;

import android.app.ActivityThread;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.sky.xposed.load.entity.PluginEntity;

import java.util.List;

import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by sky on 18-1-30.
 */

public class Main implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam param) throws Throwable {

        try {
            String packageName = param.packageName;
            Context context = getSystemContext();

//            XposedBridge.log(">>>> PackageName: " + packageName);

            handleLoadPackage(context, packageName, param);
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
    }

    private Context getSystemContext() {
        return ActivityThread.currentActivityThread().getSystemContext();
    }

    private void handleLoadPackage(Context context, String packageName, XC_LoadPackage.LoadPackageParam param) {

        ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver == null
                || BuildConfig.APPLICATION_ID.equals(packageName)
                || "android".equals(packageName)) {
            // 自己的包不处理
            return ;
        }

        Uri uri = Uri.parse("content://com.sky.xposed.load.ui.provider/package");
        Cursor cursor = contentResolver.query(
                uri, null, packageName, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            String data = cursor.getString(cursor.getColumnIndex("DATA"));
            cursor.close();

            List<PluginEntity> list = JSON.parseArray(data, PluginEntity.class);

            for (PluginEntity entity : list) {

                // 加载所有关联的插件
                handlerLoadPackage(context, param, entity);
            }
        }
    }

    private void handlerLoadPackage(Context context, XC_LoadPackage.LoadPackageParam param, PluginEntity plugin) {

        if (TextUtils.isEmpty(plugin.getPackageName())
                || TextUtils.isEmpty(plugin.getMain())) {
            XposedBridge.log("包名或入口为空,无法进行加载");
            return;
        }

        try {
//            XposedBridge.log(">>> Loading: " + plugin.getPackageName());

            // 加载Dex
            ClassLoader classLoader = loadClassLoader(context, plugin.getPackageName());

            if (classLoader == null) {
                XposedBridge.log("ClassLoader为空异常: " + plugin.getPackageName());
                return ;
            }

            // 加载相应类的入口，并调用
            Class mainClass = classLoader.loadClass(plugin.getMain());
            Object mainAny = mainClass.newInstance();

            // 直接调用
            XposedHelpers.callMethod(mainAny, "handleLoadPackage", param);
        } catch (Throwable tr) {
            XposedBridge.log("加载" + plugin.getPackageName() + "插件异常");
            XposedBridge.log(tr);
        }
    }

    /**
     * 加载指定的包的ClassLoader并返回
     */
    private ClassLoader loadClassLoader(Context context, String packageName) throws Exception {

        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;

        return new PathClassLoader(
                applicationInfo.publicSourceDir,
                applicationInfo.nativeLibraryDir,
                Thread.currentThread().getContextClassLoader());
    }
}
