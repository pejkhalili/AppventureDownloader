package com.chapdast.appventuredownloader

import android.app.IntentService
import android.content.ComponentName
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Parcel
import android.support.v4.content.FileProvider
import android.util.Log
import java.io.File

class AppCheck : IntentService("AppCheck") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if(action == applicationContext.packageName + ".AppCheck"){
                var appPackName = khttp.post(SERVER_ADDRESS,data = mapOf("m" to "packageName", "app" to APP))
                if(appPackName.statusCode == 200 && appPackName.jsonObject.getBoolean("result")){
                    Thread.sleep(TIME_IN_MILLS)
                    if(!isAppInstalled(applicationContext,appPackName.jsonObject.getString("packageName"))){
                        var dler = Intent(applicationContext,MainActivity::class.java)
                        startActivity(dler)
                    }
                }
            }
            Log.d(TAG,action.toString())
        }
    }





}
