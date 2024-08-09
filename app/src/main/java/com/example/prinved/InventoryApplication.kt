package com.example.prinved

import android.app.Application
import com.example.prinved.data.AppContainer
import com.example.prinved.data.AppDataContainer

class InventoryApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
