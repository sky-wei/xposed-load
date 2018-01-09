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

import android.app.ActivityThread
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.sky.xposed.load.data.db.entity.PluginEntity
import com.sky.xposed.load.util.Alog
import dalvik.system.PathClassLoader
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by sky on 17-12-27.
 */
class Main : IXposedHookLoadPackage, IXposedHookInitPackageResources {

    override fun handleLoadPackage(param: XC_LoadPackage.LoadPackageParam) {

        try {
            val packageName = param.packageName
            val context = getSystemContext()

//            Alog.d(">>>> PackageName: $packageName")

            handleLoadPackage(context, packageName, param)
        } catch (tr: Throwable) {
            Alog.e("处理异常", tr)
        }
    }

    private fun getSystemContext(): Context {
        return ActivityThread.currentActivityThread().systemContext
    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        // 不需要处理
    }

    private fun handleLoadPackage(context: Context, packageName: String, param: XC_LoadPackage.LoadPackageParam) {

        val uri = Uri.parse("content://com.sky.xposed.load.ui.provider/package")
        val cursor = context.contentResolver.query(
                uri, null, packageName, null, null)

        if (cursor != null && cursor.moveToFirst()) {

            val data = cursor.getString(cursor.getColumnIndex("DATA"))
            cursor.close()

            val list = JSON.parseArray(data, PluginEntity::class.java)

            // 加载所有关联的插件
            list.forEach { handlerLoadPackage(context, param, it) }
        }
    }

    private fun handlerLoadPackage(context: Context, param: XC_LoadPackage.LoadPackageParam, plugin: PluginEntity) {

        if (TextUtils.isEmpty(plugin.packageName)
                || TextUtils.isEmpty(plugin.main)) {
            Alog.e("包名或入口为空,无法进行加载")
            return
        }

        try {
            Alog.d(">>> Loading: ${plugin.packageName}")

            // 加载Dex
            val classLoader = loadClassLoader(context, plugin.packageName)

            // 加载相应类的入口，并调用
            val mainClass = classLoader.loadClass(plugin.main)
            val mainAny = mainClass.newInstance()

            // 直接调用
            XposedHelpers.callMethod(mainAny, "handleLoadPackage", param)
        } catch (tr: Throwable) {
            Alog.e("加载${plugin.packageName}插件异常", tr)
        }
    }

    /**
     * 加载指定的包的ClassLoader并返回
     */
    private fun loadClassLoader(context: Context, packageName: String): ClassLoader {

        val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
        val applicationInfo = packageInfo.applicationInfo

        return PathClassLoader(
                applicationInfo.publicSourceDir,
                applicationInfo.nativeLibraryDir,
                Thread.currentThread().contextClassLoader)
    }
}