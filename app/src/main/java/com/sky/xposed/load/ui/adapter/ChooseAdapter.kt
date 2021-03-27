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
import com.sky.xposed.app.R
import com.sky.xposed.app.databinding.ItemChooseBinding
import com.sky.xposed.load.ui.diglog.ChooseDialog

/**
 * Created by sky on 18-1-8.
 */
class ChooseAdapter<T : ChooseDialog.ChooseItem>(
        context: Context
) : BaseRecyclerAdapter<T>(context) {

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, viewType: Int): View {
        return layoutInflater.inflate(R.layout.item_choose, viewGroup, false)
    }

    override fun onCreateViewHolder(itemView: View, viewType: Int): BaseRecyclerHolder<T> {
        return ChooseHolder(itemView, this)
    }

    inner class ChooseHolder<T : ChooseDialog.ChooseItem>(
            itemView: View, baseRecyclerAdapter: BaseRecyclerAdapter<T>
    ) : BaseRecyclerHolder<T>(itemView, baseRecyclerAdapter) {

        private val binding: ItemChooseBinding by viewbind()

        override fun onInitialize() {

            binding.rlItem.setOnClickListener{
                // 回调事件
                callItemEvent(0, it, adapterPosition)
            }
        }

        override fun onBind(position: Int, viewType: Int) {

            val item = getItem(position)

            // 设置名称
            binding.tvName.text = item.getName()
        }
    }
}