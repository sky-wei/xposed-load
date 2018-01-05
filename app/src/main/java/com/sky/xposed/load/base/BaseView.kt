package com.sky.android.cherry.base

/**
 * Created by sky on 17-9-21.
 */
interface BaseView {

    fun showLoading()

    fun cancelLoading()

    fun showMessage(msg: String)
}