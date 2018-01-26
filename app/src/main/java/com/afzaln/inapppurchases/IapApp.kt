package com.afzaln.inapppurchases

import android.app.Application
import timber.log.Timber

/**
 * Created by afzal_najam on 1/25/18.
 */
class IapApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}