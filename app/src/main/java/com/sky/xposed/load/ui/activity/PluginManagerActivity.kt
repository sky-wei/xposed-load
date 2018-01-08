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
import android.content.ComponentName
import android.content.Intent
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import butterknife.BindView
import com.sky.android.common.interfaces.OnItemEventListener
import com.sky.android.common.utils.DisplayUtils
import com.sky.xposed.load.Constant
import com.sky.xposed.load.R
import com.sky.xposed.load.contract.PluginManagerContract
import com.sky.xposed.load.data.local.PluginManager
import com.sky.xposed.load.data.model.PluginModel
import com.sky.xposed.load.presenter.PluginManagerPresenter
import com.sky.xposed.load.ui.adapter.PluginListAdapter
import com.sky.xposed.load.ui.adapter.SpacesItemDecoration
import com.sky.xposed.load.ui.base.BaseActivity
import com.sky.xposed.load.ui.diglog.ChooseDialog
import com.sky.xposed.load.ui.fragment.AboutFragment
import com.sky.xposed.load.ui.fragment.ChooseAppFragment
import com.sky.xposed.load.ui.helper.RecyclerHelper
import com.sky.xposed.load.ui.util.ActivityUtil
import com.sky.xposed.load.util.Alog
import com.sky.xposed.load.util.SystemUtil


class PluginManagerActivity : BaseActivity(), OnItemEventListener,
        RecyclerHelper.OnCallback, PluginManagerContract.View {

    companion object {

        val CHOOSE_APP = 0x01

        val MODULE_SETTINGS = "de.robv.android.xposed.category.MODULE_SETTINGS"
    }

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.swipe_refresh_layout)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    private var mCurPosition = -1
    private lateinit var mPluginListAdapter: PluginListAdapter
    private lateinit var mRecyclerHelper: RecyclerHelper

    private lateinit var mPluginManagerPresenter: PluginManagerContract.Presenter

    override fun getLayoutId(): Int {
        return R.layout.activity_plugin_manager
    }

    override fun initView(intent: Intent) {

        // 设置ActionBar
        setSupportActionBar(toolbar,
                getString(R.string.app_name), false)

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)

        mPluginListAdapter = PluginListAdapter(getContext())
        mPluginListAdapter.onItemEventListener = this

        recyclerView.layoutManager = LinearLayoutManager(getContext())
        recyclerView.addItemDecoration(
                SpacesItemDecoration(DisplayUtils.dip2px(getContext(), 8f)))
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = mPluginListAdapter

        // 刷新助手类
        mRecyclerHelper = RecyclerHelper(swipeRefreshLayout, recyclerView, this)

        mPluginManagerPresenter = PluginManagerPresenter(PluginManager.INSTANCE, this)

        mRecyclerHelper.forceRefreshing()
        mPluginManagerPresenter.loadPlugins()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_plugin_manager, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.menu_settings -> {
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

    override fun onItemEvent(event: Int, view: View, position: Int, vararg args: Any?) {

        val vItem = mPluginListAdapter.getItem(position)

        when(event) {
            Constant.EventId.LONG_CLICK -> {
                // 处理长按事件
                if (vItem.status == Constant.Status.DISABLED) return

                ChooseDialog.build(getContext()){
                    stringItems { arrayOf("Hook应用信息", "关闭关联应用") }
                    onChooseListener { object : ChooseDialog.OnChooseListener{
                        override fun onChoose(position: Int, item: ChooseDialog.ChooseItem) {
                            when(position) {
                                0 -> {
                                    // 显示提示框
                                    showChooseAppDialog(vItem.packageNames)
                                }
                                1 -> {
                                    // 关闭关联的包
                                    closeHookPackages(vItem)
                                }
                            }
                        }
                    }}
                }.show()
            }
            Constant.EventId.CLICK -> {
                // 打开插件设置界面
                openXposedSettings(vItem)
            }
            Constant.EventId.SELECT -> {
                // 处理选择事件
                mCurPosition = position
                val cbSelect = view as CheckBox

                if (cbSelect.isChecked) {
                    // 选择，需要跳转到
                    ActivityUtil.startCommonActivityForResult(this,
                            getString(R.string.choose_app), ChooseAppFragment::class.java, CHOOSE_APP)
                    return
                }

                // 更新状态
                mPluginManagerPresenter.updatePlugin(
                        vItem, listOf(), Constant.Status.DISABLED)
                mPluginListAdapter.notifyDataSetChanged()
            }
        }
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
        mPluginListAdapter.notifyDataSetChanged()
    }

    override fun onUpdatePluginFailed(msg: String) {
        showMessage(msg)
    }

    private fun showChooseAppDialog(list: List<String>) {

        if (list.size == 1) {
            // 进入设置
            ActivityUtil.startAppSettingsActivity(getContext(), list[0])
            return
        }

        ChooseDialog.build(getContext()){
            stringItems { list.toTypedArray() }
            onChooseListener { object : ChooseDialog.OnChooseListener{
                override fun onChoose(position: Int, item: ChooseDialog.ChooseItem) {
                    // 进入设置
                    ActivityUtil.startAppSettingsActivity(getContext(), list[position])
                }
            }}
        }.show()
    }

    /**
     * 打开Xposed设置界面
     */
    private fun openXposedSettings(model: PluginModel) {

        try {
            val intent = Intent().apply {
                `package` = model.base.packageName
                addCategory(MODULE_SETTINGS)
            }

            val result = packageManager.queryIntentActivities(intent, 0)

            if (result != null && result.isNotEmpty()) {
                // 进入界面
                val activityInfo = result[0].activityInfo

                ActivityUtil.startActivity(getContext(), Intent().apply {
                    component = ComponentName(activityInfo.packageName, activityInfo.name)
                })
                return
            }
            showMessage("该模块未提示用户界面")
        } catch (tr: Throwable) {
            Alog.e("进入Xposed设置异常", tr)
        }
    }

    private fun closeHookPackages(model: PluginModel) {
        model.packageNames.forEach { SystemUtil.forceStop(it) }
    }
}
