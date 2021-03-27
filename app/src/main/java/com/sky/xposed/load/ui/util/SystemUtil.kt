package com.sky.xposed.load.ui.util

import android.app.ActivityManager
import android.content.Context
import com.sky.android.common.util.Alog
import com.sky.android.common.util.SystemUtil

/**
 * Created by sky on 18-1-8.
 */
object SystemUtil {

    private const val TAG = "SystemUtil"

    fun killApp(context: Context, packageName: String, root: Boolean) {

        Alog.d(">>>>>>>>> Kill: $packageName Root: $root")

        if (root) {
            // 使用Root命令Kill
            SystemUtil.execRoot("am force-stop $packageName")
            return
        }

        // 普通的方式
        killBackgroundProcesses(context, packageName)
    }

    fun killBackgroundProcesses(context: Context, packageName: String): Boolean {

        val activityManager = context.getSystemService(
                Context.ACTIVITY_SERVICE) as ActivityManager
        try {
            activityManager.killBackgroundProcesses(packageName)
            return true
        } catch (tr: Throwable) {
            Alog.e("Kill包异常", tr)
        }
        return false
    }
}