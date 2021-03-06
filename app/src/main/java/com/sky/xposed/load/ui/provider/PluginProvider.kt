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

package com.sky.xposed.load.ui.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.sky.xposed.load.data.db.DBManager
import com.sky.xposed.load.data.db.dao.HookEntityDao
import com.sky.xposed.load.data.db.entity.PluginEntity

/**
 * Created by sky on 18-1-2.
 */
class PluginProvider : ContentProvider() {

    companion object {

        val NAME = 1

        var URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI("com.sky.xposed.load.ui.provider","package", NAME)
        }
    }

    private lateinit var dbManager: DBManager

    override fun insert(uri: Uri?, values: ContentValues?): Uri? {
        return null
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {

        if (URI_MATCHER.match(uri) == NAME && !TextUtils.isEmpty(selection)) {

            val list = getPackagePluginList(selection!!)

            if (list.isEmpty()) return null

            val cursor = MatrixCursor(arrayOf("DATA"))
            cursor.newRow().add(JSON.toJSONString(list))
            return cursor
        }
        return null
    }

    override fun onCreate(): Boolean {
        dbManager = DBManager.getInstance(context)
        return true
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String {
        return ""
    }

    private fun getPackagePluginList(packageName: String): List<PluginEntity> {

        val hookEntityDao = dbManager.hookEntityDao
        val pluginEntityDao = dbManager.pluginEntityDao

        val hookEntity = hookEntityDao.queryBuilder()
                .where(HookEntityDao.Properties.PackageName.eq(packageName)).unique()

        if (hookEntity == null
                || hookEntity.pluginIds == null
                || hookEntity.pluginIds.isEmpty()) {
            return listOf()
        }

        val entityList = ArrayList<PluginEntity>()

        hookEntity.pluginIds.forEach {
            val pluginEntity = pluginEntityDao.load(it)
            if (pluginEntity != null) entityList.add(pluginEntity)
        }
        return entityList
    }
}