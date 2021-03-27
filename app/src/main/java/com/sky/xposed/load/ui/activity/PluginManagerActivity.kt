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

package com.sky.xposed.load.ui.activity

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hi.dhl.binding.viewbind
import com.sky.android.common.util.Alog
import com.sky.android.common.util.DisplayUtil
import com.sky.android.core.interfaces.OnItemEventListener
import com.sky.xposed.app.R
import com.sky.xposed.app.databinding.ActivityPluginManagerBinding
import com.sky.xposed.load.Constant
import com.sky.xposed.load.contract.PluginManagerContract
import com.sky.xposed.load.data.local.PluginManager
import com.sky.xposed.load.data.model.PluginModel
import com.sky.xposed.load.presenter.PluginManagerPresenter
import com.sky.xposed.load.ui.adapter.PluginListAdapter
import com.sky.xposed.load.ui.adapter.SpacesItemDecoration
import com.sky.xposed.load.ui.base.LoadActivity
import com.sky.xposed.load.ui.diglog.ChooseDialog
import com.sky.xposed.load.ui.fragment.AboutFragment
import com.sky.xposed.load.ui.fragment.ChooseAppFragment
import com.sky.xposed.load.ui.fragment.SettingsFragment
import com.sky.xposed.load.ui.helper.ReceiverHelper
import com.sky.xposed.load.ui.helper.RecyclerHelper
import com.sky.xposed.load.ui.service.PluginService
import com.sky.xposed.load.ui.util.ActivityUtil
import com.sky.xposed.load.ui.util.SystemUtil


class PluginManagerActivity : LoadActivity(),
        OnItemEventListener,
        RecyclerHelper.OnCallback,
        PluginManagerContract.View,
        ReceiverHelper.ReceiverCallback {

    companion object {

        const val CHOOSE_APP = 0x01

        const val MODULE_SETTINGS = "de.robv.android.xposed.category.MODULE_SETTINGS"
    }

    private var mCurPosition = -1
    private lateinit var mPluginListAdapter: PluginListAdapter
    private lateinit var mRecyclerHelper: RecyclerHelper

    private var mReceiverHelper: ReceiverHelper? = null
    private lateinit var mPluginManagerPresenter: PluginManagerContract.Presenter

    private val binding: ActivityPluginManagerBinding by viewbind()

    override val layoutId: Int
        get() = R.layout.activity_plugin_manager

    override fun initView(intent: Intent) {

        // 设置ActionBar
        setSupportActionBar(
                binding.appbarLayout.toolbar,
                getString(R.string.app_name), false
        )

        initPluginService()

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)

        mPluginListAdapter = PluginListAdapter(context)
        mPluginListAdapter.onItemEventListener = this

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.addItemDecoration(
                SpacesItemDecoration(DisplayUtil.dip2px(context, 8f)))
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = mPluginListAdapter

        // 刷新助手类
        mRecyclerHelper = RecyclerHelper(
                binding.swipeRefreshLayout,
                binding.recyclerView,
                this
        )

        mPluginManagerPresenter = PluginManagerPresenter(PluginManager.INSTANCE, this)

        mRecyclerHelper.forceRefreshing()
        mPluginManagerPresenter.loadPlugins()

        // 注册广播
        registerReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()

        // 注销广播
        unregisterReceiver()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_plugin_manager, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.menu_settings -> {
                // 进入设置界面
                ActivityUtil.startCommonActivity(
                        this, getString(R.string.settings),
                        SettingsFragment::class.java.name, false, null)
            }
            R.id.menu_about -> {
                // 进入关于界面
                ActivityUtil.startCommonActivity(
                        this, getString(R.string.about), AboutFragment::class.java)
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            mPluginListAdapter.notifyDataSetChanged()
            return
        }

        when(requestCode) {
            CHOOSE_APP -> {
                val packageNames = data!!
                        .getSerializableExtra(Constant.Key.ANY) as List<String>

                val item = mPluginListAdapter.getItem(mCurPosition)

                if (packageNames.isNotEmpty()) {
                    // 更新状态
                    mPluginManagerPresenter.updatePlugin(
                            item, packageNames, Constant.Status.ENABLED)
                    mPluginListAdapter.notifyDataSetChanged()
                    return
                }

                // 更新状态
                mPluginManagerPresenter.updatePlugin(
                        item, listOf(), Constant.Status.DISABLED)
                mPluginListAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun showLoading() {
    }

    override fun cancelLoading() {
        mRecyclerHelper.cancelRefreshing()
    }

    override fun onItemEvent(event: Int, view: View, position: Int, vararg args: Any?): Boolean {

        val vItem = mPluginListAdapter.getItem(position)

        when(event) {
            Constant.EventId.CLICK -> {
                // 打开插件设置界面
                openXposedSettings(vItem)
            }
            Constant.EventId.LONG_CLICK -> {
                // 保存当前选择下标
                mCurPosition = position

                val items = if (vItem.status == Constant.Status.DISABLED)
                    arrayOf("模块信息", "关联应用") else arrayOf("模块信息", "关联应用", "清除关联", "Hook应用信息", "关闭关联应用")

                ChooseDialog.build(context) {
                    stringItems { items }
                    onChooseListener {
                        object : ChooseDialog.OnChooseListener {
                            override fun onChoose(position: Int, item: ChooseDialog.ChooseItem) {
                                when (position) {
                                    0 -> {
                                        showChooseAppDialog(listOf(vItem.base.packageName))
                                    }
                                    1 -> {
                                        // 选择，需要跳转到
                                        val args = Bundle().apply {
                                            putSerializable(Constant.Key.ANY, ArrayList<String>(vItem.packageNames))
                                        }
                                        ActivityUtil.startCommonActivityForResult(this@PluginManagerActivity,
                                                getString(R.string.choose_app), ChooseAppFragment::class.java, args, CHOOSE_APP)
                                    }
                                    2 -> {
                                        // 关闭关联应用
                                        closeHookPackages(vItem)

                                        // 更新状态
                                        mPluginManagerPresenter.updatePlugin(
                                                vItem, listOf(), Constant.Status.DISABLED)
                                        mPluginListAdapter.notifyDataSetChanged()
                                    }
                                    3 -> {
                                        showChooseAppDialog(vItem.packageNames)
                                    }
                                    4 -> {
                                        closeHookPackages(vItem)
                                    }
                                }
                            }
                        }
                    }
                }.show()
            }
        }
        return true
    }

    override fun onRefresh() {
        mPluginManagerPresenter.loadPlugins()
    }

    override fun onLoadMore() {
    }

    override fun onLoadPlugins(models: List<PluginModel>) {
        mPluginListAdapter.items = models
        mPluginListAdapter.notifyDataSetChanged()
    }

    override fun onLoadPluginsFailed(msg: String) {
        showMessage(msg)
    }

    override fun onUpdatePlugin(model: PluginModel) {

        // 关闭关联应用
        closeHookPackages(model)

        mPluginListAdapter.notifyDataSetChanged()
    }

    override fun onUpdatePluginFailed(msg: String) {
        showMessage(msg)
    }

    override fun onReceive(action: String, intent: Intent) {

        if (Intent.ACTION_PACKAGE_ADDED == action
                || Intent.ACTION_PACKAGE_REMOVED == action) {

            // 重新加载
            mPluginManagerPresenter.loadPlugins()
        }
    }

    private fun registerReceiver() {

        val intentFilter = IntentFilter().apply {
            addAction("android.intent.action.PACKAGE_ADDED")
            addAction("android.intent.action.PACKAGE_REPLACED")
            addAction("android.intent.action.PACKAGE_REMOVED")
            addDataScheme("package")
        }

        mReceiverHelper = ReceiverHelper(this, this, intentFilter)
        mReceiverHelper?.registerReceiver()
    }

    private fun unregisterReceiver() {

        mReceiverHelper?.unregisterReceiver()
        mReceiverHelper = null
    }

    private fun showChooseAppDialog(list: List<String>) {

        if (list.size == 1) {
            // 进入设置
            ActivityUtil.startAppSettingsActivity(context, list[0])
            return
        }

        ChooseDialog.build(context){
            stringItems { list.toTypedArray() }
            onChooseListener { object : ChooseDialog.OnChooseListener{
                override fun onChoose(position: Int, item: ChooseDialog.ChooseItem) {
                    // 进入设置
                    ActivityUtil.startAppSettingsActivity(context, list[position])
                }
            }}
        }.show()
    }

    private fun initPluginService() {

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val autoKill = preferences.getBoolean(Constant.Preference.AUTO_KILL_APP, false)

        if (autoKill) startService(Intent(context, PluginService::class.java))
    }

    /**
     * 打开Xposed设置界面
     */
    private fun openXposedSettings(model: PluginModel) {

        var intent = Intent().apply {
            `package` = model.base.packageName
            addCategory(MODULE_SETTINGS)
        }

        if (openXposedSettings(intent)) {
            // 启动成功
            return
        }

        // 获取默认的
        intent = Intent(Intent.ACTION_MAIN).apply {
            `package` = model.base.packageName
        }

        if (openXposedSettings(intent)) {
            // 启动成功
            return
        }

        showMessage("该模块未配置用户界面")
    }

    /**
     * 打开Xposed设置界面
     */
    private fun openXposedSettings(intent: Intent): Boolean {

        try {
            // 获取相应的入口
            val result = packageManager.queryIntentActivities(intent, 0)

            if (result != null && result.isNotEmpty()) {
                // 进入界面
                val activityInfo = result[0].activityInfo

                return ActivityUtil.startActivity(context, Intent().apply {
                    component = ComponentName(activityInfo.packageName, activityInfo.name)
                })
            }
        } catch (tr: Throwable) {
            Alog.e("进入Xposed设置异常", tr)
        }
        return false
    }

    private fun closeHookPackages(model: PluginModel) {

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val rootKillApp = preferences.getBoolean(Constant.Preference.ROOT_KILL_APP, false)

        model.packageNames.forEach { SystemUtil.killApp(context, it, rootKillApp) }
    }
}
