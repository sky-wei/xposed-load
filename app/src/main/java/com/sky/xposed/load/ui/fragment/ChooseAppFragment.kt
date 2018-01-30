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

package com.sky.xposed.load.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.CompoundButton
import android.widget.EditText
import butterknife.BindView
import com.sky.android.common.interfaces.OnItemEventListener
import com.sky.android.common.utils.DisplayUtils
import com.sky.xposed.app.R
import com.sky.xposed.load.Constant
import com.sky.xposed.load.contract.ChooseAppContract
import com.sky.xposed.load.data.local.PluginManager
import com.sky.xposed.load.data.model.AppModel
import com.sky.xposed.load.presenter.ChooseAppPresenter
import com.sky.xposed.load.ui.adapter.AppListAdapter
import com.sky.xposed.load.ui.adapter.SpacesItemDecoration
import com.sky.xposed.load.ui.base.BaseFragment
import com.sky.xposed.load.ui.diglog.ChooseDialog
import com.sky.xposed.load.ui.helper.RecyclerHelper
import java.io.Serializable

/**
 * Created by sky on 18-1-7.
 */
class ChooseAppFragment : BaseFragment(), TextWatcher, OnItemEventListener,
        ChooseAppContract.View, RecyclerHelper.OnCallback {

    @BindView(R.id.et_search)
    lateinit var etSearch: EditText
    @BindView(R.id.swipe_refresh_layout)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    private lateinit var mAppListAdapter: AppListAdapter
    private lateinit var mRecyclerHelper: RecyclerHelper
    private var mSelectPackage: ArrayList<String>? = null

    lateinit var mChooseAppPresenter: ChooseAppContract.Presenter

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_choose_app, container, false)
    }

    override fun initView(view: View, args: Bundle?) {

        mSelectPackage = args?.getSerializable(Constant.Key.ANY) as? ArrayList<String>

        setHasOptionsMenu(true)
        etSearch.addTextChangedListener(this)

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)

        mAppListAdapter = AppListAdapter(context)
        mAppListAdapter.onItemEventListener = this

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(
                SpacesItemDecoration(DisplayUtils.dip2px(context, 8f)))
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = mAppListAdapter

        // 刷新助手类
        mRecyclerHelper = RecyclerHelper(swipeRefreshLayout, recyclerView, this)

        mChooseAppPresenter = ChooseAppPresenter(PluginManager.INSTANCE, this)

        mRecyclerHelper.forceRefreshing()
        mChooseAppPresenter.loadApps()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_choose_app, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {

            R.id.menu_filter -> {
                // 应用过滤
                ChooseDialog.build(context){
                    stringItems { arrayOf("用户安装", "系统应用", "所有应用") }
                    onChooseListener { object : ChooseDialog.OnChooseListener{
                        override fun onChoose(position: Int, item: ChooseDialog.ChooseItem) {
                            when(position) {
                                0 -> { mChooseAppPresenter.setFilter(Constant.Filter.USER) }
                                1 -> { mChooseAppPresenter.setFilter(Constant.Filter.SYSTEM) }
                                2 -> { mChooseAppPresenter.setFilter(Constant.Filter.ALL) }
                            }
                            etSearch.setText("")
                            mRecyclerHelper.forceRefreshing()
                            mChooseAppPresenter.loadApps()
                        }
                    }}
                }.show()
            }
            R.id.menu_ok -> {
                // 获取选择的包名
                val packageNames = mAppListAdapter.selectApp.map { it.key }
                val dataIntent = Intent().apply {
                    putExtra(Constant.Key.ANY, packageNames as Serializable)
                }
                activity.setResult(Activity.RESULT_OK, dataIntent)
                activity.onBackPressed()
            }
        }
        return true
    }

    override fun onLoadApps(models: List<AppModel>) {
        mAppListAdapter.items = models

        mSelectPackage?.forEach {
            // 选择相应的应用
            mAppListAdapter.selectApp(it)
        }

        mAppListAdapter.notifyDataSetChanged()
    }

    override fun onLoadAppsFailed(msg: String) {
        showMessage(msg)
    }

    override fun onSearchApp(models: List<AppModel>) {
        onLoadApps(models)
    }

    override fun onSearchAppFailed(msg: String) {
        showMessage(msg)
    }

    override fun afterTextChanged(s: Editable) {
        mChooseAppPresenter.searchApp(s.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onItemEvent(event: Int, view: View, position: Int, vararg args: Any?) {

        when(event) {
            Constant.EventId.SELECT -> {

                val tView = view as CompoundButton
                val item = mAppListAdapter.getItem(position)

                // 选择图片
                mAppListAdapter.selectApp(item.packageName, tView.isChecked)
            }
        }
    }

    override fun showLoading() {
    }

    override fun cancelLoading() {
        mRecyclerHelper.cancelRefreshing()
    }

    override fun onRefresh() {
        etSearch.setText("")
        mChooseAppPresenter.loadApps()
    }

    override fun onLoadMore() {
    }
}