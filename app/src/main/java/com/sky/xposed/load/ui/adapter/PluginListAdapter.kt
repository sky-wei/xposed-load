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

package com.sky.xposed.load.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnLongClick
import com.sky.android.common.adapter.SimpleRecyclerAdapter
import com.sky.android.common.base.BaseRecyclerAdapter
import com.sky.android.common.base.BaseRecyclerHolder
import com.sky.xposed.load.R
import com.sky.xposed.load.data.model.PluginModel

/**
 * Created by sky on 18-1-5.
 */
class PluginListAdapter(context: Context) : SimpleRecyclerAdapter<PluginModel>(context) {

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, viewType: Int): View {
        return layoutInflater.inflate(R.layout.item_plugin_list, viewGroup, false)
    }

    override fun onCreateViewHolder(view: View, viewType: Int): BaseRecyclerHolder<PluginModel> {
        return PluginHolder(view, this)
    }

    inner class PluginHolder(itemView: View, adapter: BaseRecyclerAdapter<PluginModel>)
        : BaseRecyclerHolder<PluginModel>(itemView, adapter) {

        @BindView(R.id.iv_image)
        lateinit var ivImage: ImageView
        @BindView(R.id.tv_name)
        lateinit var tvName: TextView
        @BindView(R.id.tv_desc)
        lateinit var tvDesc: TextView
        @BindView(R.id.ck_select)
        lateinit var ckSelect: CheckBox

        override fun onInitialize() {
            ButterKnife.bind(this, itemView)
        }

        override fun onBind(position: Int, viewType: Int) {

            val item = getItem(position)

            // 设置信息
//            Glide.with(context)
//                    .load(item.imgSrc)
//                    .into(ivImage)

//            tvTitle.text = if (TextUtils.isEmpty(item.lTitle)) item.title else item.lTitle
//            tvSource.text = item.source
        }

        @OnClick(R.id.card_view, R.id.ck_select)
        fun onClick(view: View) {

        }

        @OnLongClick(R.id.card_view)
        fun onLongClick(view: View): Boolean {
            return true
        }
    }
}