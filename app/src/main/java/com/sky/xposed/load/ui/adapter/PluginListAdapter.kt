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
import com.hi.dhl.binding.viewbind
import com.sky.android.core.adapter.BaseRecyclerAdapter
import com.sky.android.core.adapter.BaseRecyclerHolder
import com.sky.android.core.adapter.SimpleRecyclerAdapter
import com.sky.xposed.app.R
import com.sky.xposed.app.databinding.ItemPluginListBinding
import com.sky.xposed.load.Constant
import com.sky.xposed.load.data.model.PluginModel

/**
 * Created by sky on 18-1-5.
 */
class PluginListAdapter(
        context: Context
) : SimpleRecyclerAdapter<PluginModel>(context) {

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, viewType: Int): View {
        return layoutInflater.inflate(R.layout.item_plugin_list, viewGroup, false)
    }

    override fun onCreateViewHolder(view: View, viewType: Int): BaseRecyclerHolder<PluginModel> {
        return PluginHolder(view, this)
    }

    inner class PluginHolder(itemView: View, adapter: BaseRecyclerAdapter<PluginModel>)
        : BaseRecyclerHolder<PluginModel>(itemView, adapter) {

        private val binding: ItemPluginListBinding by viewbind()

        override fun onInitialize() {

            binding.cardView.setOnClickListener {
                callItemEvent(Constant.EventId.CLICK, it, adapterPosition)
            }
            binding.ivMore.setOnClickListener {
                callItemEvent(Constant.EventId.LONG_CLICK, it, adapterPosition)
            }

            binding.cardView.setOnLongClickListener {
                callItemEvent(Constant.EventId.LONG_CLICK, it, adapterPosition)
            }
        }

        override fun onBind(position: Int, viewType: Int) {

            val item = getItem(position)

            // 设置信息
            binding.ivImage.setImageDrawable(item.base.image)
            binding.tvName.text = item.base.label
            binding.tvDesc.text = "包名: ${item.base.packageName}\n版本: v" +
                    "${item.base.versionName}\nHook: ${item.packageNames}"
        }
    }
}