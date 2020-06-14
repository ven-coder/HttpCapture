package com.levine.httpcapturedemo

import android.app.Application
import com.levine.http_capture.HCLib

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        HCLib.init(this)
    }
}