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
import com.sky.xposed.app.databinding.ItemAppListBinding
import com.sky.xposed.load.Constant
import com.sky.xposed.load.data.model.AppModel

/**
 * Created by sky on 18-1-5.
 */
class AppListAdapter(
        context: Context
) : SimpleRecyclerAdapter<AppModel>(context) {

    var selectApp = HashMap<String, String>()

    fun selectApp(packageName: String, select: Boolean = true) {

        if (select) {
            // 添加
            selectApp[packageName] = packageName
            return
        }
        // 删除
        selectApp.remove(packageName)
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, viewType: Int): View {
        return layoutInflater.inflate(R.layout.item_app_list, viewGroup, false)
    }

    override fun onCreateViewHolder(view: View, viewType: Int): BaseRecyclerHolder<AppModel> {
        return AppHolder(view, this)
    }

    inner class AppHolder(itemView: View, adapter: BaseRecyclerAdapter<AppModel>)
        : BaseRecyclerHolder<AppModel>(itemView, adapter) {

        private val binding: ItemAppListBinding by viewbind()

        override fun onInitialize() {
            super.onInitialize()

            binding.cardView.setOnClickListener{
                binding.ckSelect.isChecked = !binding.ckSelect.isChecked
                callItemEvent(Constant.EventId.SELECT, binding.ckSelect, adapterPosition)
            }
        }

        override fun onBind(position: Int, viewType: Int) {

            val item = getItem(position)

            // 设置信息
            binding.ivImage.setImageDrawable(item.image)
            binding.tvName.text = item.label
            binding.tvDesc.text = "包名: ${item.packageName}\n版本: v" +
                    "${item.versionName}\n版本号: ${item.versionCode}"
            binding.ckSelect.isChecked = selectApp.containsKey(item.packageName)
        }
    }
}