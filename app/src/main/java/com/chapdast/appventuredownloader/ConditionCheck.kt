package com.chapdast.appventuredownloader

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.util.Log

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class ConditionCheck : IntentService("ConditionCheck") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if(action == applicationContext.packageName+".ConditionCheck") {
                var push = Intent(applicationContext, Push::class.java)
                push.action = applicationContext.packageName + ".Push"
                Log.d("SERCCC", push.action)
                stopService(push)
                startService(push)
                Log.d("SERCCC", "Launching Service...")
            }
        }
    }


}
