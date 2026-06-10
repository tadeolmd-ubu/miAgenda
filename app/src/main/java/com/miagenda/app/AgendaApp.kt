package com.miagenda.app

import android.app.Application
import com.miagenda.app.di.AppContainer

class AgendaApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
