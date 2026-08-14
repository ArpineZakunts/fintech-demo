package com.example.fintechdemo

import android.app.Application
import com.example.fintechdemo.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FintechDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FintechDemoApp)
            modules(appModules)
        }
    }
}
