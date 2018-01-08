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
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.sky.android.common.base.BaseRecyclerAdapter
import com.sky.android.common.base.BaseRecyclerHolder
import com.sky.xposed.load.R
import com.sky.xposed.load.ui.diglog.ChooseDialog

/**
 * Created by sky on 18-1-8.
 */
class ChooseAdapter<T : ChooseDialog.ChooseItem>(context: Context) : BaseRecyclerAdapter<T>(context) {

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, viewType: Int): View {
        return layoutInflater.inflate(R.layout.item_choose, viewGroup, false)
    }

    override fun onCreateViewHolder(itemView: View, viewType: Int): BaseRecyclerHolder<T> {
        return ChooseHolder(itemView, this)
    }

    inner class ChooseHolder<T : ChooseDialog.ChooseItem>(itemView: View, baseRecyclerAdapter: BaseRecyclerAdapter<T>)
        : BaseRecyclerHolder<T>(itemView, baseRecyclerAdapter) {

        @BindView(R.id.tv_name)
        lateinit var tvName: TextView

        override fun onInitialize() {
            ButterKnife.bind(this, itemView)
        }

        override fun onBind(position: Int, viewType: Int) {

            val item = getItem(position)

            // 设置名称
            tvName.text = item.getName()
        }

        @OnClick(R.id.rl_item)
        fun onClick(view: View) {
            // 回调事件
            onItemEvent(0, view, adapterPosition)
        }
    }
}