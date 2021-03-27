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

package com.sky.xposed.load.ui.diglog

import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hi.dhl.binding.viewbind
import com.sky.android.core.interfaces.OnItemEventListener
import com.sky.xposed.app.R
import com.sky.xposed.app.databinding.DialogChooseBinding
import com.sky.xposed.load.ui.adapter.ChooseAdapter

/**
 * Created by sky on 18-1-8.
 */
class ChooseDialog private constructor(
        context: Context,
        private val items: List<ChooseItem>,
        private var chooseListener: OnChooseListener
) : BottomSheetDialog(context), OnItemEventListener {

    private val binding: DialogChooseBinding by viewbind()
    private val mChooseAdapter: ChooseAdapter<ChooseItem>

    private constructor(builder: Builder)
            : this(builder.context, builder.items, builder.chooseListener) {

        if (builder.cancelListener != null)
            setOnCancelListener(builder.cancelListener)
    }

    init {
        setContentView(R.layout.dialog_choose)

        val layoutManager = LinearLayoutManager(getContext())
        binding.recycleView.layoutManager = layoutManager

        mChooseAdapter = ChooseAdapter(getContext())
        mChooseAdapter.items = items
        mChooseAdapter.onItemEventListener = this

        binding.recycleView.adapter = mChooseAdapter
    }

    override fun onItemEvent(event: Int, view: View, position: Int, vararg args: Any): Boolean {
        this.dismiss()
        chooseListener?.onChoose(position, items[position])
        return true
    }

    companion object {

        fun build(context: Context, init: Builder.() -> Unit) = Builder(context, init).build()
    }

    class Builder private constructor(val context: Context) {

        lateinit var items: List<ChooseItem>
        lateinit var chooseListener: OnChooseListener
        var cancelable = true
        var cancelListener: DialogInterface.OnCancelListener? = null

        constructor(context: Context, init: Builder.() -> Unit): this(context) {
            init()
        }

        fun build() = ChooseDialog(this)

        fun items(init: Builder.() -> List<ChooseItem>) = apply { items = init() }

        fun resItems(init: Builder.() -> Int) = apply {
            val strItems = context.resources.getTextArray(init())
            items = StringChooseItem.asList(*strItems)
        }

        fun stringItems(init: Builder.() -> Array<String>) = apply { this.items = StringChooseItem.asList(*init()) }

        fun cancelable(init: Builder.() -> Boolean) = apply { cancelable = init() }

        fun onCancelListener(init: Builder.() -> DialogInterface.OnCancelListener) = apply { cancelListener = init() }

        fun onChooseListener(init: Builder.() -> OnChooseListener) = apply { chooseListener = init() }
    }

    class StringChooseItem(private val name: CharSequence) : ChooseItem {

        override fun getName(): CharSequence {
            return name
        }

        companion object {

            fun asList(vararg chars: CharSequence): List<StringChooseItem> {
                return chars.map { StringChooseItem(it) }
            }
        }
    }

    interface ChooseItem {

        fun getName(): CharSequence
    }

    interface OnChooseListener {

        fun onChoose(position: Int, item: ChooseItem)
    }
}