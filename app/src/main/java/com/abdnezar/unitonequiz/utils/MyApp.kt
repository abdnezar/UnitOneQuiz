package com.abdnezar.unitonequiz.utils

import android.app.Application

class MyApp : Application() {

    companion object {
        const val BASE_URL = "http://104.248.27.63/"
        const val HOME_URL = "home/public"
        lateinit var instance: MyApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}