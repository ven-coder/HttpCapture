package com.levine.http_capture

import android.util.Log

object HCLog {
    fun log(strings: Array<String>) {
        Log.d(Constant.LOG_TAG, "↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-LOG-↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓")
        for (string in strings) {
            Log.d(Constant.LOG_TAG, string)
        }
        Log.d(Constant.LOG_TAG, "↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑-LOG-↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑")
    }
}