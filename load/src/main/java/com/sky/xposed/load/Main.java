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
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.sky.xposed.load.entity.PluginEntity;

import java.util.ArrayList;
import java.util.List;

import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by sky on 18-1-30.
 */

public class Main implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam param) throws Throwable {

        try {
//            XposedBridge.log(">>>> PackageName: " + packageName);
            handleLoadPackage(getSystemContext(), param);
        } catch (Throwable e) {
            XposedBridge.log(">> error " + param.packageName);
            XposedBridge.log(e);
        }
    }

    /**
     * 获取系统的Context
     * @return
     */
    private Context getSystemContext() {
        return ActivityThread.currentActivityThread().getSystemContext();
    }

    /**
     * 处理加载的包
     * @param context
     * @param param
     */
    private void handleLoadPackage(
            Context context, XC_LoadPackage.LoadPackageParam param
    ) {
        final String packageName = param.packageName;
        final ApplicationInfo info = param.appInfo;

        if (BuildConfig.APPLICATION_ID.equals(packageName)
                || "com.sky.xposed.app".equals(packageName)
                || info == null
                || (info.flags & ApplicationInfo.FLAG_SYSTEM) != 0
        ) {
            // 不需要处理
            return ;
        }

        List<PluginEntity> entities = loadPlugin(context, packageName);

        if (entities == null) {
            // 不需要处理
            return;
        }

        for (PluginEntity entity : entities) {
            // 加载所有关联的插件
            handlerLoadPackage(context, param, entity);
        }
    }

    /**
     * 加载插件实例
     * @param context
     * @param packageName
     * @return
     */
    private List<PluginEntity> loadPlugin(Context context, String packageName) {

        ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver == null) {
            // 不需要处理
            return null;
        }

        Cursor cursor = null;
        List<PluginEntity> entities = new ArrayList<>();

        try {
            Uri uri = Uri.parse("content://com.sky.xposed.load/package/" + packageName);
            cursor = contentResolver.query(
                    uri, null, null, null, null
            );

            while (cursor != null && cursor.moveToNext()) {

                int nameIndex = cursor.getColumnIndex("name");
                int mainIndex = cursor.getColumnIndex("main");

                String name = cursor.getString(nameIndex);
                String main = cursor.getString(mainIndex);

                entities.add(new PluginEntity(name, main));
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return entities;
    }

    /**
     * 处理加载的包
     * @param context
     * @param param
     * @param plugin
     */
    private void handlerLoadPackage(
            Context context, XC_LoadPackage.LoadPackageParam param, PluginEntity plugin
    ) {

        if (TextUtils.isEmpty(plugin.getPackageName())
                || TextUtils.isEmpty(plugin.getMain())) {
            XposedBridge.log("包名或入口为空,无法进行加载");
            return;
        }

        try {
//            XposedBridge.log(">>> Loading: " + plugin.getPackageName());

            // 加载Dex
            ClassLoader classLoader = createClassLoader(context, plugin.getPackageName());

            // 创建插件的对象
            Class<?> mainClass = classLoader.loadClass(plugin.getMain());
            IXposedHookLoadPackage moduleInstance = (IXposedHookLoadPackage) mainClass.newInstance();

            // 直接调用
            moduleInstance.handleLoadPackage(param);
        } catch (Throwable tr) {
            XposedBridge.log("加载" + plugin.getPackageName() + "插件异常");
            XposedBridge.log(tr);
        }
    }

    /**
     * 创建指定的包的ClassLoader并返回
     */
    private ClassLoader createClassLoader(Context context, String packageName) throws Exception {

        ApplicationInfo applicationInfo = context
                .getPackageManager().getApplicationInfo(packageName, 0);

        return new PathClassLoader(
                applicationInfo.publicSourceDir,
                applicationInfo.nativeLibraryDir,
                XposedBridge.BOOTCLASSLOADER
        );
    }
}
