package com.sofit

import android.app.Application

class AppClass : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this@AppClass
    }

    companion object {
        private var instance: AppClass? = null
        fun getContext(): AppClass {
            return instance!!
        }
    }
}