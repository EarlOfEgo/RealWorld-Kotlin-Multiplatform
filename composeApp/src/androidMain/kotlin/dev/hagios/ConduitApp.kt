package dev.hagios

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

class ConduitApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
    }
}