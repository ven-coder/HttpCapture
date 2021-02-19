package com.levine.httpcapturedemo

import android.app.Application
import com.levine.netcapture.NCP

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        NCP.getInstance(this).init()
    }
}