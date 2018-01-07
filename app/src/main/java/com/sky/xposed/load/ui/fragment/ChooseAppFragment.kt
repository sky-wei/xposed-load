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

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import butterknife.BindView
import com.sky.android.common.interfaces.OnItemEventListener
import com.sky.android.common.utils.DisplayUtils
import com.sky.xposed.load.Constant
import com.sky.xposed.load.R
import com.sky.xposed.load.contract.ChooseAppContract
import com.sky.xposed.load.data.local.PluginManager
import com.sky.xposed.load.data.model.AppModel
import com.sky.xposed.load.presenter.ChooseAppPresenter
import com.sky.xposed.load.ui.adapter.AppListAdapter
import com.sky.xposed.load.ui.adapter.SpacesItemDecoration
import com.sky.xposed.load.ui.base.BaseFragment

/**
 * Created by sky on 18-1-7.
 */
class ChooseAppFragment : BaseFragment(), TextWatcher, OnItemEventListener, ChooseAppContract.View {

    @BindView(R.id.et_search)
    lateinit var etSearch: EditText
    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    lateinit var mAppListAdapter: AppListAdapter
    lateinit var mChooseAppPresenter: ChooseAppContract.Presenter

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_choose_app, container, false)
    }

    override fun initView(view: View, args: Bundle?) {

        etSearch.addTextChangedListener(this)

        mAppListAdapter = AppListAdapter(context)
        mAppListAdapter.onItemEventListener = this

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(
                SpacesItemDecoration(DisplayUtils.dip2px(context, 8f)))
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = mAppListAdapter

        mChooseAppPresenter = ChooseAppPresenter(PluginManager.INSTANCE, this)
        mChooseAppPresenter.loadApps(0)
    }

    override fun onLoadApps(models: List<AppModel>) {
        mAppListAdapter.items = models
        mAppListAdapter.notifyDataSetChanged()
    }

    override fun onLoadAppsFailed(msg: String) {
        showMessage(msg)
    }

    override fun onSearchApp(models: List<AppModel>) {
        mAppListAdapter.items = models
        mAppListAdapter.notifyDataSetChanged()
    }

    override fun onSearchAppFailed(msg: String) {
        showMessage(msg)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onItemEvent(event: Int, view: View, position: Int, vararg args: Any?) {

        when(event) {
            Constant.EventId.SELECT -> {

                val cbSelect = view as CheckBox
            }
        }
    }
}