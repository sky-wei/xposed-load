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

package com.sky.xposed.load.ui.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.preference.PreferenceManager
import com.sky.xposed.load.Constant
import com.sky.xposed.load.data.db.DBManager
import com.sky.xposed.load.data.db.dao.PluginEntityDao
import com.sky.xposed.load.ui.helper.ReceiverHelper
import com.sky.xposed.load.ui.util.SystemUtil


/**
 * Created by sky on 18-1-9.
 */
class PluginService : Service(), ReceiverHelper.ReceiverCallback {

    private var mReceiverHelper: ReceiverHelper? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val intentFilter = IntentFilter().apply {
            addAction("android.intent.action.PACKAGE_ADDED")
            addAction("android.intent.action.PACKAGE_REPLACED")
            addAction("android.intent.action.PACKAGE_REMOVED")
            addDataScheme("package")
        }

        mReceiverHelper = ReceiverHelper(this, this, intentFilter)
        mReceiverHelper?.registerReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()

        mReceiverHelper?.unregisterReceiver()
        mReceiverHelper = null
    }

    override fun onReceive(action: String, intent: Intent) {

        if (Intent.ACTION_PACKAGE_ADDED == action
                || Intent.ACTION_PACKAGE_REMOVED == action
                || Intent.ACTION_PACKAGE_REPLACED == action) {

            val packageName = intent.data?.schemeSpecificPart?:return

            // 获取安装的包名
            onHandlerPackage(packageName)
        }
    }

    private fun onHandlerPackage(packageName: String) {

        // 获取存在的插件信息
        val pluginEntityDao = DBManager.getInstance(this).pluginEntityDao

        val pluginEntity = pluginEntityDao.queryBuilder()
                .where(PluginEntityDao.Properties.PackageName.eq(packageName))
                .unique()

        if (pluginEntity != null
                && pluginEntity.status == Constant.Status.ENABLED) {
            // 关闭应用
            killHookPackages(pluginEntity.hookPackageNames)
        }
    }

    private fun killHookPackages(packageNames: List<String>) {

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val rootKillApp = preferences.getBoolean(Constant.Preference.ROOT_KILL_APP, false)

        packageNames.forEach { SystemUtil.killApp(this, it, rootKillApp) }
    }
}