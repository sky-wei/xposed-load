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

import android.content.Intent
import android.os.Bundle
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
import com.sky.xposed.load.ui.fragment.AboutFragment
import com.sky.xposed.load.ui.fragment.ChooseAppFragment
import com.sky.xposed.load.ui.helper.RecyclerHelper
import com.sky.xposed.load.ui.util.ActivityUtil
import java.io.Serializable

class PluginManagerActivity : BaseActivity(), OnItemEventListener,
        RecyclerHelper.OnCallback, PluginManagerContract.View {

    companion object {

        val CHOOSE_APP = 0x01
    }

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.swipe_refresh_layout)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

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

    override fun showLoading() {
    }

    override fun cancelLoading() {
        mRecyclerHelper.cancelRefreshing()
    }

    override fun onItemEvent(event: Int, view: View, position: Int, vararg args: Any?) {

        when(event) {
            Constant.EventId.LONG_CLICK -> {

            }
            Constant.EventId.CLICK -> {

            }
            Constant.EventId.SELECT -> {

                val cbSelect = view as CheckBox
                val item = mPluginListAdapter.getItem(position)

                if (cbSelect.isChecked) {

                    val args = Bundle().apply {
                        putSerializable(Constant.Key.ANY, item.packageNames as Serializable)
                    }

                    // 选择，需要跳转到
                    ActivityUtil.startCommonActivityForResult(this,
                            getString(R.string.choose_app), ChooseAppFragment::class.java, args, CHOOSE_APP)
                    return
                }
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
}
