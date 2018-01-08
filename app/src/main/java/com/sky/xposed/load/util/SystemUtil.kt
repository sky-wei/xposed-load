package com.sky.xposed.load.util

import android.text.TextUtils
import com.sky.android.common.utils.FileUtils
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by sky on 18-1-8.
 */
object SystemUtil {

    val TAG = "SystemUtil"

    fun forceStop(packageName: String) {

        if (TextUtils.isEmpty(packageName)) return

        val cmd = "am force-stop " + packageName

        Alog.d(TAG, "CMD: " + cmd)

        try {
            val runtime = Runtime.getRuntime()
            runtime.exec(cmd)
        } catch (e: Throwable) {
            Alog.e(TAG, "执行命令异常", e)
        }
    }

    /**
     * 执行Root权限的命令
     * @param cmd
     */
    fun execRoot(cmd: String): ExecResult {

        if (TextUtils.isEmpty(cmd)) {
            throw NullPointerException("执行任务不能为空")
        }

        return exec("su -c " + cmd)
    }

    /**
     * 执行指定的命令
     * @param cmd
     */
    fun exec(cmd: String): ExecResult {

        if (TextUtils.isEmpty(cmd)) {
            throw NullPointerException("执行任务不能为空")
        }

        var process: Process? = null
        var inputStream: BufferedReader? = null
        var errorStream: BufferedReader? = null

        val execResult = ExecResult()

        try {
            process = Runtime.getRuntime().exec(cmd)

            inputStream = BufferedReader(
                    InputStreamReader(process!!.inputStream))
            errorStream = BufferedReader(
                    InputStreamReader(process.errorStream))

            var line: String
            val inputString = StringBuilder()
            val errorString = StringBuilder()

            // 获取返回结果状态
            execResult.result = process.waitFor()

//            while ((line = inputStream.readLine()) != null)
//                inputString.append(line)
//
//            while ((line = errorStream.readLine()) != null)
//                errorString.append(line)

            execResult.errorMsg = errorString.toString()
            execResult.successMsg = inputString.toString()
        } catch (e: Throwable) {
            Alog.e("执行命令异常", e)
            execResult.errorMsg = e.message
        } finally {
            FileUtils.closeQuietly(errorStream)
            FileUtils.closeQuietly(inputStream)
            if (process != null) process.destroy()
        }
        return execResult
    }

    class ExecResult {

        var result = -1
        var errorMsg: String? = null
        var successMsg: String? = null

        override fun toString(): String {
            return "ExecResult{" +
                    "result=" + result +
                    ", errorMsg='" + errorMsg + '\'' +
                    ", successMsg='" + successMsg + '\'' +
                    '}'
        }
    }
}