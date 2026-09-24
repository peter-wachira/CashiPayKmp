package com.peterwachira.cashipay

import android.app.Application
import com.peterwachira.cashipay.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Initializes application-wide dependencies when the Android process starts.
 */
class CashiPayApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger(Level.DEBUG)
            }

            allowOverride(false)
            androidContext(this@CashiPayApplication)
            modules(appModule)
        }
    }
}
