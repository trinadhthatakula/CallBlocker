package com.rams.ted.callblocker

import android.content.Context
import android.content.IntentFilter
import android.os.Build
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.rams.ted.callblocker.model.PhoneCallReceiver

class CallBlockerApplication: MultiDexApplication() {

    private val callReceiver = PhoneCallReceiver()

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            registerReceiver(callReceiver, IntentFilter().apply {
                addAction("android.intent.action.PHONE_STATE")
                addAction("android.intent.action.NEW_OUTGOING_CALL")
            })


    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onTerminate() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            unregisterReceiver(callReceiver)
        super.onTerminate()
    }

}