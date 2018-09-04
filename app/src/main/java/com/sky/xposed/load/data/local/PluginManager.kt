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

package com.sky.xposed.load.data.local

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.sky.android.common.utils.FileUtils
import com.sky.xposed.load.Constant
import com.sky.xposed.load.data.db.DBManager
import com.sky.xposed.load.data.db.dao.HookEntityDao
import com.sky.xposed.load.data.db.entity.HookEntity
import com.sky.xposed.load.data.db.entity.PluginEntity
import com.sky.xposed.load.data.local.info.PluginInfo
import com.sky.xposed.load.data.model.AppModel
import com.sky.xposed.load.data.model.PluginModel
import com.sky.xposed.load.util.Alog
import rx.Observable
import java.io.InputStream
import java.util.zip.ZipFile

/**
 * Created by sky on 18-1-5.
 */
class PluginManager private constructor() {

    private object Holder { val INSTANCE = PluginManager() }

    companion object {

        val TAG = "PluginManager"

        val INSTANCE: PluginManager by lazy { Holder.INSTANCE }
    }

    private var mInitialize = false
    private lateinit var mContext: Context

    fun initialize(context: Context) {

        if (mInitialize) return

        mInitialize = true
        mContext = context
    }

    fun release() {

        if (!mInitialize) return
    }

    fun loadPlugins(): Observable<List<PluginModel>> {
        return loadPlugins{ it == Constant.Load.PACKAGE_NAME }
    }

    fun updatePlugin(model: PluginModel, packageNames: List<String>, status: Int): Observable<PluginModel> {
        return onUnsafeCreate{ updateLocalPlugin(model, packageNames, status) }
    }

    fun loadPlugins(filter: (packageName: String) -> Boolean): Observable<List<PluginModel>> {
        return onUnsafeCreate{ loadLocalPlugins(filter) }
    }

    fun loadApps(filter: Int): Observable<List<AppModel>> {
        return onUnsafeCreate{ loadLocalApps{
            when(filter) {
                Constant.Filter.USER -> {
                    (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0 ||  it.packageName == Constant.Load.PACKAGE_NAME
                }
                Constant.Filter.SYSTEM -> {
                    (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 ||  it.packageName == Constant.Load.PACKAGE_NAME
                }
                else -> it.packageName == Constant.Load.PACKAGE_NAME
            }
        }}
    }

    fun loadLocalApps(filter: (applicationInfo: ApplicationInfo) -> Boolean): List<AppModel> {

        val appList = ArrayList<AppModel>()

        val packageList = mContext.packageManager.getInstalledPackages(0)

        packageList.forEach {

            val info = it.applicationInfo

            if (info != null && info.enabled
                    && !filter.invoke(info)) {
                // 添加到列表中
                appList.add(newAppInfo(mContext.packageManager, it))
            }
        }

        return appList
    }

    fun loadLocalPlugins(filter: (packageName: String) -> Boolean): List<PluginModel> {

        val pluginModelList = ArrayList<PluginModel>()

        val pluginInfoList = loadLocalPluginInfo(filter)

        if (pluginInfoList.isEmpty()) return listOf()

        val dbManager = DBManager.getInstance(mContext)
        val pluginEntityDao = dbManager.pluginEntityDao
        val pluginEntityList = pluginEntityDao.loadAll()

        pluginInfoList.forEach {

            val info = it
            var entity = pluginEntityList.firstOrNull { info.packageName == it.packageName }

            if (entity == null) {
                // 添加到数据库中
                entity = PluginEntity().apply {
                    this.packageName = info.packageName
                    this.hookPackageNames = listOf()
                    this.main = info.main
                    this.status = Constant.Status.DISABLED
                }

                // 添加到数据库中
                pluginEntityDao.insert(entity)
            }

            if (entity.main != it.main) {
                // 更新信息
                entity.main = it.main
                pluginEntityDao.update(entity)
            }

            // 添加到列表中
            pluginModelList.add(PluginModel(
                    entity.id, entity.status,
                    transformNullList(entity.hookPackageNames), it))
        }

        return pluginModelList
    }

    fun loadLocalPluginInfo(filter: (packageName: String) -> Boolean): List<PluginInfo> {

        val pluginList = ArrayList<PluginInfo>()

        val packageList = mContext.packageManager
                .getInstalledPackages(PackageManager.GET_META_DATA)

        packageList.forEach {

            val info = it.applicationInfo

            if (info != null && info.enabled
                    && info.metaData != null
                    && info.metaData.containsKey("xposedmodule")
                    && !filter.invoke(info.packageName)) {

                // 添加到列表中
                pluginList.add(newPluginInfo(mContext.packageManager, it))
            }
        }

        // 附加的
        addVClubApp(pluginList, "club.vxv.vx.wxload")
        addVClubApp(pluginList, "club.vxv.vx.vxcore")

        return pluginList
    }

    fun addVClubApp(list: ArrayList<PluginInfo>, packageName: String) {

        try {
            val packageManager = mContext.packageManager
            val packageInfo = packageManager.getPackageInfo(packageName, 0)

            val applicationInfo = packageInfo.applicationInfo

            val packageName = packageInfo.packageName
            val versionName = packageInfo.versionName
            val versionCode = packageInfo.versionCode
            val label = applicationInfo.loadLabel(packageManager).toString()
            val image = applicationInfo.loadIcon(packageManager)
            val main = getVClubPluginMain(packageInfo)

            // 添加到列表中
            list.add(PluginInfo(label, packageName, versionName, versionCode, image, main))
        } catch (tr: Throwable) {
            Alog.e("加载异常", tr)
        }
    }

    fun newPluginInfo(packageManager: PackageManager, packageInfo: PackageInfo): PluginInfo {


        val applicationInfo = packageInfo.applicationInfo

        val packageName = packageInfo.packageName
        val versionName = packageInfo.versionName
        val versionCode = packageInfo.versionCode
        val label = applicationInfo.loadLabel(packageManager).toString()
        val image = applicationInfo.loadIcon(packageManager)
        val main = getPluginMain(packageInfo)

        return PluginInfo(label, packageName, versionName, versionCode, image, main)
    }

    fun newAppInfo(packageManager: PackageManager, packageInfo: PackageInfo): AppModel {

        val applicationInfo = packageInfo.applicationInfo

        val packageName = packageInfo.packageName
        val versionName = packageInfo.versionName
        val versionCode = packageInfo.versionCode
        val label = applicationInfo.loadLabel(packageManager).toString()
        val image = applicationInfo.loadIcon(packageManager)

        return AppModel(label, packageName, versionName, versionCode, image)
    }

    fun getPluginMain(packageInfo: PackageInfo): String {

        var stream: InputStream? = null
        var apkFile: ZipFile? = null
        val applicationInfo = packageInfo.applicationInfo

        try {
            apkFile = ZipFile(applicationInfo.publicSourceDir)
            val entry = apkFile.getEntry("assets/xposed_init") ?: return ""

            stream = apkFile.getInputStream(entry)
            // 获取入口信息
            return stream.bufferedReader().readLine()
        } catch (tr: Throwable) {
            Alog.e("获取入口信息异常")
        } finally {
            FileUtils.closeQuietly(stream)
            FileUtils.closeQuietly(apkFile)
        }
        return ""
    }

    fun getVClubPluginMain(packageInfo: PackageInfo): String {

        var stream: InputStream? = null
        var apkFile: ZipFile? = null
        val applicationInfo = packageInfo.applicationInfo

        try {
            apkFile = ZipFile(applicationInfo.publicSourceDir)
            val entry = apkFile.getEntry("raw/vclub") ?: return ""

            stream = apkFile.getInputStream(entry)
            // 获取入口信息
            return stream.bufferedReader().readLine()
        } catch (tr: Throwable) {
            Alog.e("获取入口信息异常")
        } finally {
            FileUtils.closeQuietly(stream)
            FileUtils.closeQuietly(apkFile)
        }
        return ""
    }

    fun updateLocalPlugin(model: PluginModel, packageNames: List<String>, status: Int): PluginModel {

        val dbManager = DBManager.getInstance(mContext)
        val pluginEntityDao = dbManager.pluginEntityDao
        val hookEntityDao = dbManager.hookEntityDao

        model.packageNames.forEach {

            val hookEntity = hookEntityDao.queryBuilder().where(
                    HookEntityDao.Properties.PackageName.eq(it)).unique()

            if (hookEntity != null) {
                // 更新数据
                hookEntity.pluginIds = removeListId(hookEntity.pluginIds, model.id)
                hookEntityDao.update(hookEntity)
            }
        }

        packageNames.forEach {

            val hookEntity = hookEntityDao.queryBuilder().where(
                    HookEntityDao.Properties.PackageName.eq(it)).unique()

            if (hookEntity == null) {
                // 添加到数据库中
                hookEntityDao.insert(HookEntity().apply {
                    this.packageName = it
                    this.pluginIds = listOf(model.id)
                })
            } else {
                hookEntity.pluginIds = addIdToList(hookEntity.pluginIds, model.id)
                hookEntityDao.update(hookEntity)
            }
        }

        model.packageNames = packageNames
        model.status = status

        val pluginEntity = pluginEntityDao.load(model.id)
        pluginEntity.hookPackageNames = packageNames
        pluginEntity.status = status

        // 更新数据
        pluginEntityDao.update(pluginEntity)

        return model
    }

    fun <T> onUnsafeCreate(next: () -> T): Observable<T> {

        return Observable.unsafeCreate<T> {

            try {
                it.onNext(next.invoke())
                it.onCompleted()
            } catch (tr: Throwable) {
                Alog.e(TAG, "处理异常", tr)
                it.onError(tr)
            }
        }
    }

    fun <T> transformNullList(dto: List<T>?): List<T> {
        return dto ?: listOf()
    }

    fun removeListId(ids: List<Long>?, id: Long): List<Long> {

        if (ids == null) return listOf()

        val result = ArrayList<Long>()

        ids.forEach { if (it != id) result.add(it) }

        return result
    }

    fun addIdToList(ids: List<Long>?, id: Long): List<Long> {

        if (ids == null) return listOf(id)

        val result = ArrayList<Long>()

        result.addAll(ids)
        result.add(id)

        return result
    }
}