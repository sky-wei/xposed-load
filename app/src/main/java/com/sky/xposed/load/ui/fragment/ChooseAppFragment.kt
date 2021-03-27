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
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.hi.dhl.binding.viewbind
import com.sky.android.common.util.DisplayUtil
import com.sky.android.core.interfaces.OnItemEventListener
import com.sky.xposed.app.R
import com.sky.xposed.app.databinding.FragmentChooseAppBinding
import com.sky.xposed.load.Constant
import com.sky.xposed.load.contract.ChooseAppContract
import com.sky.xposed.load.data.local.PluginManager
import com.sky.xposed.load.data.model.AppModel
import com.sky.xposed.load.presenter.ChooseAppPresenter
import com.sky.xposed.load.ui.adapter.AppListAdapter
import com.sky.xposed.load.ui.adapter.SpacesItemDecoration
import com.sky.xposed.load.ui.base.LoadFragment
import com.sky.xposed.load.ui.diglog.ChooseDialog
import com.sky.xposed.load.ui.helper.RecyclerHelper
import java.io.Serializable

/**
 * Created by sky on 18-1-7.
 */
class ChooseAppFragment : LoadFragment(), TextWatcher, OnItemEventListener,
        ChooseAppContract.View, RecyclerHelper.OnCallback {

    private lateinit var mAppListAdapter: AppListAdapter
    private lateinit var mRecyclerHelper: RecyclerHelper
    private var mSelectPackage: ArrayList<String>? = null

    private lateinit var mChooseAppPresenter: ChooseAppContract.Presenter

    private val binding: FragmentChooseAppBinding by viewbind()

    override val layoutId: Int
        get() = R.layout.fragment_choose_app

    override fun initView(view: View, args: Bundle?) {

        mSelectPackage = args?.getSerializable(Constant.Key.ANY) as? ArrayList<String>

        setHasOptionsMenu(true)
        binding.etSearch.addTextChangedListener(this)

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)

        mAppListAdapter = AppListAdapter(context)
        mAppListAdapter.onItemEventListener = this

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.addItemDecoration(
                SpacesItemDecoration(DisplayUtil.dip2px(context, 8f)))
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = mAppListAdapter

        // 刷新助手类
        mRecyclerHelper = RecyclerHelper(
                binding.swipeRefreshLayout,
                binding.recyclerView,
                this
        )

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
                            binding.etSearch.setText("")
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
                activity?.setResult(Activity.RESULT_OK, dataIntent)
                activity?.onBackPressed()
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

    override fun onItemEvent(event: Int, view: View, position: Int, vararg args: Any?): Boolean {

        when(event) {
            Constant.EventId.SELECT -> {

                val tView = view as CompoundButton
                val item = mAppListAdapter.getItem(position)

                // 选择图片
                mAppListAdapter.selectApp(item.packageName, tView.isChecked)
            }
        }
        return true
    }

    override fun showLoading() {
    }

    override fun cancelLoading() {
        mRecyclerHelper.cancelRefreshing()
    }

    override fun onRefresh() {
        binding.etSearch.setText("")
        mChooseAppPresenter.loadApps()
    }

    override fun onLoadMore() {
    }
}