package com.example.temple_run

import android.app.Application
import com.example.temple_run.utilies.SignalManager


class App: Application() {

    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
    }
}