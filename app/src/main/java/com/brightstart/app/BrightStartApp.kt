package com.brightstart.app

import android.app.Application

class BrightStartApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: BrightStartApp
            private set
    }
}
